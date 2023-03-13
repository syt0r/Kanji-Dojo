package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticePreviewScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.toScreenType

class PracticePreviewReloadStateUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val practiceRepository: PracticeRepository,
    private val fetchGroupItemsUseCase: PracticePreviewScreenContract.FetchGroupItemsUseCase,
    private val filterGroupItemsUseCase: PracticePreviewScreenContract.FilterGroupItemsUseCase,
    private val sortGroupItemsUseCase: PracticePreviewScreenContract.SortGroupItemsUseCase,
    private val createGroupsUseCase: PracticePreviewScreenContract.CreatePracticeGroupsUseCase,
) : PracticePreviewScreenContract.ReloadDataUseCase {

    override suspend fun load(
        practiceId: Long,
        previousState: ScreenState.Loaded?
    ): ScreenState.Loaded {
        val configuration = previousState?.configuration ?: getRepositoryConfiguration()
        val items = fetchGroupItemsUseCase.fetch(practiceId)

        val groups = filterGroupItemsUseCase
            .filter(items, configuration.practiceType, configuration.filterOption)
            .let {
                sortGroupItemsUseCase.sort(it, configuration.sortOption, configuration.isDescending)
            }
            .let {
                createGroupsUseCase.create(it, configuration.practiceType)
            }

        val selectedGroups = previousState?.selectedGroupIndexes
            ?.let { previouslySelectedGroups ->
                val currentlyAvailableGroupIndexes = groups.map { it.index }.toSet()
                previouslySelectedGroups.intersect(currentlyAvailableGroupIndexes)
            }
            ?: emptySet()


        return ScreenState.Loaded(
            title = practiceRepository.getPracticeInfo(practiceId).name,
            configuration = configuration,
            items = items,
            groups = groups,
            isMultiselectEnabled = previousState?.isMultiselectEnabled ?: false,
            selectedGroupIndexes = selectedGroups
        )
    }

    private suspend fun getRepositoryConfiguration(): PracticePreviewScreenConfiguration {
        val default = PracticePreviewScreenConfiguration()
        return userPreferencesRepository.run {
            PracticePreviewScreenConfiguration(
                practiceType = getPracticeType()?.toScreenType() ?: default.practiceType,
                filterOption = getFilterOption()?.toScreenType() ?: default.filterOption,
                sortOption = getSortOption()?.toScreenType() ?: default.sortOption,
                isDescending = getIsSortDescending() ?: default.isDescending
            )
        }
    }

}