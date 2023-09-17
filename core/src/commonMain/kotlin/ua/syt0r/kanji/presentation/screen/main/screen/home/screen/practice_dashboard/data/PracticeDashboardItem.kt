package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data

import kotlin.time.Duration

data class PracticeDashboardItem(
    val practiceId: Long,
    val title: String,
    val timeSinceLastPractice: Duration?,
    val writingProgress: PracticeStudyProgress,
    val readingProgress: PracticeStudyProgress
)

data class PracticeStudyProgress(
    val known: Int,
    val review: Int,
    val new: Int,
    val quickLearn: List<String>,
    val quickReview: List<String>
) {

    val total = known + review + new
    val completionPercentage = (known + review).toFloat() / total * 100

}