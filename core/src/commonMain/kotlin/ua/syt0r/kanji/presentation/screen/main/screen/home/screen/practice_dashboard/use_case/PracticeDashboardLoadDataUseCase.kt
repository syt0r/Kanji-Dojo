package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.use_case

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.PracticeDashboardItem

class PracticeDashboardLoadDataUseCase(
    private val practiceRepository: PracticeRepository
) : PracticeDashboardScreenContract.LoadDataUseCase {

    override fun load(): List<PracticeDashboardItem> {
        val now = Clock.System.now()
        return practiceRepository.getAllPractices()
            .map {
                val writingReviewTime = practiceRepository.getLatestWritingReviewTime(it.id)
                val readingReviewTime = practiceRepository.getLatestReadingReviewTime(it.id)
                val reviewTime = listOfNotNull(writingReviewTime, readingReviewTime).maxOrNull()
                PracticeDashboardItem(
                    practiceId = it.id,
                    title = it.name,
                    reviewToNowDuration = reviewTime
                        ?.toInstant(TimeZone.currentSystemDefault())
                        ?.let { reviewInstant -> now.minus(reviewInstant) }
                )
            }
    }

}
