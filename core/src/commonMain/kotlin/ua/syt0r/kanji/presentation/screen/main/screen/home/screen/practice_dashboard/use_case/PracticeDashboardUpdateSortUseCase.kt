package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.use_case

import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeReorderRequestData

class PracticeDashboardUpdateSortUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val practiceRepository: PracticeRepository
) : PracticeDashboardScreenContract.UpdateSortUseCase {

    override suspend fun update(data: PracticeReorderRequestData) {
        userPreferencesRepository.dashboardSortByTime.set(data.sortByTime)
        practiceRepository.updatePracticePositions(
            practiceIdToPositionMap = data.reorderedList.reversed()
                .mapIndexed { index, item -> item.practiceId to index }
                .toMap()
        )
    }

}