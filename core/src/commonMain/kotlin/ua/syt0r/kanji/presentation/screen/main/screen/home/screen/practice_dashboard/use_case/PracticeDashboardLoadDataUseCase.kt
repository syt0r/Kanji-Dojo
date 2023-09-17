package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.use_case

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import ua.syt0r.kanji.core.app_state.AppStateManager
import ua.syt0r.kanji.core.app_state.DailyGoalLimitOption
import ua.syt0r.kanji.core.app_state.DeckStudyProgress
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.DailyIndicatorData
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.DailyProgress
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.PracticeDashboardItem
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.PracticeStudyProgress
import kotlin.math.max
import kotlin.math.min

class PracticeDashboardLoadDataUseCase(
    private val appStateManager: AppStateManager
) : PracticeDashboardScreenContract.LoadDataUseCase {

    override fun load() = flow<ScreenState> {
        appStateManager.appStateFlow
            .onEach { loadableData ->
                if (loadableData.isLoading) {
                    emit(ScreenState.Loading)
                    return@onEach
                }

                val appState = loadableData.lastData!!

                val configuration = appState.dailyGoalConfiguration
                val progress = appState.dailyProgress

                val totalNew = appState.decks.sumOf {
                    it.writingDetails.new.size + it.readingDetails.new.size
                }

                val totalReview = appState.decks.sumOf {
                    it.writingDetails.review.size + it.readingDetails.review.size
                }

                val leftToStudy = if (configuration.learnLimit is DailyGoalLimitOption.Limited) {
                    max(0, min(configuration.learnLimit.limit - progress.studied, totalNew))
                } else 0

                val leftToReview = if (configuration.learnLimit is DailyGoalLimitOption.Limited) {
                    max(0, min(configuration.learnLimit.limit - progress.studied, totalReview))
                } else 0

                val loadedState = ScreenState.Loaded(
                    practiceSets = appState.decks.map { deckInfo ->
                        PracticeDashboardItem(
                            practiceId = deckInfo.id,
                            title = deckInfo.title,
                            timeSinceLastPractice = deckInfo.timeSinceLastReview,
                            writingProgress = deckInfo.writingDetails.toPracticeStudyProgress(
                                leftToStudy,
                                leftToReview
                            ),
                            readingProgress = deckInfo.readingDetails.toPracticeStudyProgress(
                                leftToStudy,
                                leftToReview
                            )
                        )
                    },
                    dailyIndicatorData = DailyIndicatorData(
                        configuration = appState.dailyGoalConfiguration,
                        progress = getDailyProgress(leftToStudy, leftToReview)
                    )
                )
                emit(loadedState)
            }
            .collect()
    }

    private fun DeckStudyProgress.toPracticeStudyProgress(
        leftToStudy: Int,
        leftToReview: Int
    ): PracticeStudyProgress {
        return PracticeStudyProgress(
            known = done.size,
            review = review.size,
            new = new.size,
            quickLearn = new.take(leftToStudy),
            quickReview = review.take(leftToReview)
        )
    }

    private fun getDailyProgress(
        leftToStudy: Int,
        leftToReview: Int
    ): DailyProgress {
        return when {
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
