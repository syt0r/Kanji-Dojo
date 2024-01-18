package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.use_case

import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeMergeRequestData

class MergePracticeSetsUseCase(
    private val repository: PracticeRepository
) : PracticeDashboardScreenContract.MergePracticeSetsUseCase {

    override suspend fun merge(data: PracticeMergeRequestData) {
        repository.createPracticeAndMerge(data.title, data.practiceIdList)
    }

}