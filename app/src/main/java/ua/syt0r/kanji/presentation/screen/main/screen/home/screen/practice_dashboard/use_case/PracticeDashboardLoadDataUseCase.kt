package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.use_case

import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.PracticeDashboardItem
import javax.inject.Inject

class PracticeDashboardLoadDataUseCase @Inject constructor(
    private val practiceRepository: UserDataContract.PracticeRepository
) : PracticeDashboardScreenContract.LoadDataUseCase {

    override fun load(): List<PracticeDashboardItem> {
        return practiceRepository.getAllPractices()
            .map {
                val writingReviewTime = practiceRepository.getLatestWritingReviewTime(it.id)
                val readingReviewTime = practiceRepository.getLatestReadingReviewTime(it.id)
                PracticeDashboardItem(
                    practiceId = it.id,
                    title = it.name,
                    reviewTime = listOfNotNull(writingReviewTime, readingReviewTime).maxOrNull()
                )
            }
    }

}
