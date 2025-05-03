package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.use_case

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import ua.syt0r.kanji.core.RefreshableData
import ua.syt0r.kanji.core.logger.runWithTimeLog
import ua.syt0r.kanji.core.mergeSharedFlows
import ua.syt0r.kanji.core.refreshableDataProducerFlow
import ua.syt0r.kanji.core.srs.LetterPracticeType
import ua.syt0r.kanji.core.srs.LetterSrsDeck
import ua.syt0r.kanji.core.srs.LetterSrsManager
import ua.syt0r.kanji.core.srs.SrsDecksData
import ua.syt0r.kanji.core.srs.VocabPracticeType
import ua.syt0r.kanji.core.srs.VocabSrsDeck
import ua.syt0r.kanji.core.srs.VocabSrsManager
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.database.ReviewHistoryRepository
import ua.syt0r.kanji.core.user_data.database.StreakData
import ua.syt0r.kanji.core.user_data.preferences.PreferencesContract
import ua.syt0r.kanji.presentation.LifecycleState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.GeneralDashboardScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.GeneralDashboardStats
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.LetterStudyTargetPracticeOptions
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.StreakCalendarItem
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.StudyTarget
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.StudyTargetProgress
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.StudyTargetState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.general_dashboard.VocabStudyTargetPracticeOptions
import kotlin.time.Duration.Companion.days

interface SubscribeOnGeneralDashboardScreenDataUseCase {

    operator fun invoke(
        coroutineScope: CoroutineScope,
        lifecycleState: StateFlow<LifecycleState>
    ): Flow<RefreshableData<ScreenState.Loaded>>

}

class DefaultSubscribeOnGeneralDashboardScreenDataUseCase(
    private val letterSrsManager: LetterSrsManager,
    private val vocabSrsManager: VocabSrsManager,
    private val preferencesRepository: PreferencesContract.AppPreferences,
    private val reviewHistoryRepository: ReviewHistoryRepository,
    private val timeUtils: TimeUtils
) : SubscribeOnGeneralDashboardScreenDataUseCase {

    override fun invoke(
        coroutineScope: CoroutineScope,
        lifecycleState: StateFlow<LifecycleState>
    ): Flow<RefreshableData<ScreenState.Loaded>> = refreshableDataProducerFlow(
        dataChangeFlow = mergeSharedFlows(
            coroutineScope,
            letterSrsManager.dataChangeFlow,
            vocabSrsManager.dataChangeFlow
        ),
        lifecycleState = lifecycleState,
        producer = { withContext(Dispatchers.IO) { produceState() } }
    )

    private suspend fun ProducerScope<ScreenState.Loaded>.produceState() {
        val deferredLettersDecks = async {
            runWithTimeLog("letterDecksData") { letterSrsManager.getDecks() }
        }

        val deferredVocabDecks = async {
            runWithTimeLog("vocabDecksData") { vocabSrsManager.getDecks() }
        }

        val deferredStreakData = async {
            runWithTimeLog("streakData") { getStats() }
        }

        val preferencesStudyTargets = preferencesRepository.generalDashboardStudyTargets.get()
            .mapNotNull { (name, enabled) ->
                val target = StudyTarget.entries.find { it.name == name } ?: return@mapNotNull null
                target to enabled
            }

        val missingStudyTargets = StudyTarget.entries
            .minus(preferencesStudyTargets.map { it.first })
            .map { it to false }

        val studyTargets = mutableStateOf(
            preferencesStudyTargets.plus(missingStudyTargets).map { (studyTarget, enabled) ->
                async {
                    StudyTargetState(
                        studyTarget = studyTarget,
                        enabled = enabled,
                        progress = when (studyTarget.practiceType) {
                            is LetterPracticeType -> getPracticeTypeProgress(
                                decksData = deferredLettersDecks.await(),
                                practiceType = studyTarget.practiceType
                            )

                            is VocabPracticeType -> getPracticeTypeProgress(
                                decksData = deferredVocabDecks.await(),
                                practiceType = studyTarget.practiceType
                            )
                        }
                    )
                }
            }.awaitAll()
        )

        val state = ScreenState.Loaded(
            studyTargets = studyTargets,
            stats = deferredStreakData.await()
        )
        send(state)

        snapshotFlow { studyTargets.value }
            .drop(1)
            .onEach {
                val updatedMap = it.associate { it.studyTarget.name to it.enabled }
                preferencesRepository.generalDashboardStudyTargets.set(updatedMap)
            }
            .launchIn(this)

    }

    private fun CoroutineScope.getPracticeTypeProgress(
        decksData: SrsDecksData<LetterSrsDeck, LetterPracticeType>,
        practiceType: LetterPracticeType
    ): StudyTargetProgress {
        if (decksData.decks.isEmpty()) return StudyTargetProgress.NoDecks

        val combinedDailyNew = mutableMapOf<String, Long>()
        val combinedDailyDue = mutableMapOf<String, Long>()
        val combinedNotNew = mutableSetOf<String>()

        decksData.decks
            .map { it.id to it.progressMap.getValue(practiceType) }
            .forEach { (deckId, srsProgress) ->
                srsProgress.dailyNew.associateWith { deckId }
                    .forEach { combinedDailyNew[it.key] = it.value }
                srsProgress.dailyDue.associateWith { deckId }
                    .forEach { combinedDailyDue[it.key] = it.value }

                combinedNotNew.addAll(srsProgress.due)
                combinedNotNew.addAll(srsProgress.done)
            }

        val leftover = decksData.dailyProgress.leftoversByPracticeTypeMap.getValue(practiceType)

        return StudyTargetProgress.WithDecks(
            options = LetterStudyTargetPracticeOptions(
                newToDeckIdMap = combinedDailyNew.toList().take(leftover.new).toMap(),
                dueToDeckIdMap = combinedDailyDue.toList().take(leftover.due).toMap()
            ),
            totalProgress = combinedNotNew.size.toFloat() / decksData.uniqueCardsCount
        )
    }

    private fun CoroutineScope.getPracticeTypeProgress(
        decksData: SrsDecksData<VocabSrsDeck, VocabPracticeType>,
        practiceType: VocabPracticeType
    ): StudyTargetProgress {
        if (decksData.decks.isEmpty()) return StudyTargetProgress.NoDecks

        val combinedDailyNew = mutableMapOf<Long, Long>()
        val combinedDailyDue = mutableMapOf<Long, Long>()
        val combinedNotNew = mutableSetOf<Long>()

        decksData.decks
            .map { it.id to it.progressMap.getValue(practiceType) }
            .forEach { (deckId, srsProgress) ->
                srsProgress.dailyNew.associateWith { deckId }
                    .forEach { combinedDailyNew[it.key] = it.value }
                srsProgress.dailyDue.associateWith { deckId }
                    .forEach { combinedDailyDue[it.key] = it.value }

                combinedNotNew.addAll(srsProgress.due)
                combinedNotNew.addAll(srsProgress.done)
            }

        val leftover = decksData.dailyProgress.leftoversByPracticeTypeMap.getValue(practiceType)

        return StudyTargetProgress.WithDecks(
            options = VocabStudyTargetPracticeOptions(
                newToDeckIdMap = combinedDailyNew.toList().take(leftover.new).toMap(),
                dueToDeckIdMap = combinedDailyDue.toList().take(leftover.due).toMap()
            ),
            totalProgress = combinedNotNew.size.toFloat() / decksData.uniqueCardsCount
        )
    }

    private suspend fun getStats(): GeneralDashboardStats {

        fun StreakData.includesDate(date: LocalDate): Boolean {
            return date in start..end
        }

        val streaks = reviewHistoryRepository.getStreaks()
        val longestStreak = streaks.maxByOrNull { it.length }

        val currentDate = timeUtils.getCurrentDate()
        val streakCalendarStartDate = currentDate.minus(
            value = STREAK_CALENDAR_DAYS - 1,
            unit = DateTimeUnit.DAY
        )

        val displayDateRange = streakCalendarStartDate..currentDate
        val streaksForCalendar = streaks.filter { it.end in displayDateRange }
        val streakCalendarItems = mutableListOf<StreakCalendarItem>()

        var date = streakCalendarStartDate
        do {
            val hasReviews = streaksForCalendar.any { it.includesDate(date) }
            streakCalendarItems.add(StreakCalendarItem(date, hasReviews))
            date = date.plus(1, DateTimeUnit.DAY)
        } while (date <= currentDate)

        val currentStreakSearchDates = setOf(
            currentDate,
            currentDate.minus(1, DateTimeUnit.DAY)
        )
        val currentStreak = streaks.find { streak ->
            currentStreakSearchDates.any { streak.includesDate(it) }
        }

        val reviewsToday = timeUtils.getCurrentDate()
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .let {
                reviewHistoryRepository.getReviews(
                    start = it,
                    end = it.plus(1.days)
                )
            }
            .size

        return GeneralDashboardStats(
            currentStreak = currentStreak?.length ?: 0,
            longestStreak = longestStreak?.length ?: 0,
            reviewsToday = reviewsToday
        )
    }

    companion object {
        private const val STREAK_CALENDAR_DAYS = 7
    }

}