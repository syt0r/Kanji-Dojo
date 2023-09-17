package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data

import ua.syt0r.kanji.core.app_state.DailyGoalConfiguration

data class DailyIndicatorData(
    val configuration: DailyGoalConfiguration,
    val progress: DailyProgress
)

sealed interface DailyProgress {
    object Completed : DailyProgress
    data class StudyOnly(val count: Int) : DailyProgress
    data class ReviewOnly(val count: Int) : DailyProgress
    data class StudyAndReview(val study: Int, val review: Int) : DailyProgress
}