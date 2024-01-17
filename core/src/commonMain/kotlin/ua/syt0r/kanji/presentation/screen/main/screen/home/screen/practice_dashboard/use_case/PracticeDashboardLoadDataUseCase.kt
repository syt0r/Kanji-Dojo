package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.use_case

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import ua.syt0r.kanji.core.app_state.AppStateManager
import ua.syt0r.kanji.core.app_state.DailyGoalConfiguration
import ua.syt0r.kanji.core.app_state.DeckStudyProgress
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.DailyIndicatorData
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.DailyProgress
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardItem
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenData
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeStudyProgress
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

class PracticeDashboardLoadDataUseCase(
    private val appStateManager: AppStateManager
) : PracticeDashboardScreenContract.LoadDataUseCase {

    override fun load() = flow<PracticeDashboardScreenData> {
        appStateManager.appStateFlow
            .filter { !it.isLoading }
            .onEach { loadableData ->

                val appState = loadableData.lastData!!

                val configuration = appState.dailyGoalConfiguration
                val progress = appState.dailyProgress

                val totalNew = appState.decks.flatMap { it.writingDetails.new }.distinct().size +
                        appState.decks.flatMap { it.readingDetails.new }.distinct().size

                val totalReview = appState.decks.flatMap { it.writingDetails.review }.distinct()
                    .size + appState.decks.flatMap { it.readingDetails.review }.distinct().size

                val leftToStudy = max(
                    a = 0,
                    b = min(configuration.learnLimit - progress.studied, totalNew)
                )

                val leftToReview = max(
                    a = 0,
                    b = min(configuration.reviewLimit - progress.reviewed, totalReview)
                )

                val loadedState = PracticeDashboardScreenData(
                    items = appState.decks
                        .map { deckInfo ->
                            PracticeDashboardItem(
                                practiceId = deckInfo.id,
                                title = deckInfo.title,
                                timeSinceLastPractice = deckInfo.timeSinceLastReview,
                                writingProgress = deckInfo.writingDetails.toPracticeStudyProgress(
                                    configuration = appState.dailyGoalConfiguration,
                                    leftToStudy = leftToStudy,
                                    leftToReview = leftToReview
                                ),
                                readingProgress = deckInfo.readingDetails.toPracticeStudyProgress(
                                    configuration = appState.dailyGoalConfiguration,
                                    leftToStudy = leftToStudy,
                                    leftToReview = leftToReview
                                )
                            )
                        }
                        .sortedBy { it.timeSinceLastPractice ?: Duration.INFINITE },
                    dailyIndicatorData = DailyIndicatorData(
                        configuration = appState.dailyGoalConfiguration,
                        progress = getDailyProgress(
                            configuration = appState.dailyGoalConfiguration,
                            leftToStudy = leftToStudy,
                            leftToReview = leftToReview
                        )
                    )
                )
                emit(loadedState)
            }
            .collect()
    }

    private fun DeckStudyProgress.toPracticeStudyProgress(
        configuration: DailyGoalConfiguration,
        leftToStudy: Int,
        leftToReview: Int
    ): PracticeStudyProgress {
        return PracticeStudyProgress(
            known = done.size,
            review = review.size,
            new = new.size,
            quickLearn = if (configuration.enabled) new.take(leftToStudy) else new,
            quickReview = if (configuration.enabled) review.take(leftToReview) else review
        )
    }

    private fun getDailyProgress(
        configuration: DailyGoalConfiguration,
        leftToStudy: Int,
        leftToReview: Int
    ): DailyProgress {
        return when {
            !configuration.enabled -> DailyProgress.Disabled
            leftToStudy > 0 && leftToReview > 0 -> DailyProgress.StudyAndReview(
                leftToStudy,
                leftToReview
            )

            leftToStudy == 0 && leftToReview > 0 -> DailyProgress.ReviewOnly(leftToReview)
            leftToStudy > 0 && leftToReview == 0 -> DailyProgress.StudyOnly(leftToStudy)
            else -> DailyProgress.Completed
        }
    }

}
