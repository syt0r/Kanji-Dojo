package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case

import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticePreviewLayout
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticePreviewScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.SortOption
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.toScreenType

class PracticePreviewReloadStateUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val practiceRepository: PracticeRepository,
    private val fetchItemsUseCase: PracticePreviewScreenContract.FetchItemsUseCase,
    private val filterItemsUseCase: PracticePreviewScreenContract.FilterItemsUseCase,
    private val sortItemsUseCase: PracticePreviewScreenContract.SortItemsUseCase,
    private val createGroupsUseCase: PracticePreviewScreenContract.CreatePracticeGroupsUseCase,
) : PracticePreviewScreenContract.ReloadDataUseCase {

    override suspend fun load(
        practiceId: Long,
        previousState: ScreenState.Loaded?
    ): ScreenState.Loaded {
        val configuration = previousState?.configuration ?: getRepositoryConfiguration()

        val items = fetchItemsUseCase.fetch(practiceId)
        val visibleItems = filterItemsUseCase
            .filter(items, configuration.practiceType, configuration.filterOption)
            .let {
                sortItemsUseCase.sort(it, configuration.sortOption, configuration.isDescending)
            }

        val title = practiceRepository.getPracticeInfo(practiceId).name
        val isSelectionModeEnabled = previousState?.isSelectionModeEnabled ?: false

        val sharePractice = sortItemsUseCase.sort(items, SortOption.ADD_ORDER, false)
            .joinToString("") { it.character }

        return when (configuration.layout) {
            PracticePreviewLayout.SingleCharacter -> {
                ScreenState.Loaded.Items(
                    title = title,
                    configuration = configuration,
                    allItems = items,
                    sharePractice = sharePractice,
                    isSelectionModeEnabled = isSelectionModeEnabled,
                    selectedItems = previousState.let { it as? ScreenState.Loaded.Items }
                        ?.let {
                            it.selectedItems.intersect(other = items.map { it.character }.toSet())
                        }
                        ?: emptySet(),
                    visibleItems = visibleItems
                )
            }

            PracticePreviewLayout.Groups -> {
                val groupsCreationResult = createGroupsUseCase.create(
                    items = items,
                    visibleItems = visibleItems,
                    type = configuration.practiceType,
                    probeKanaGroups = configuration.kanaGroups
                )

                ScreenState.Loaded.Groups(
                    title = title,
                    configuration = configuration,
                    allItems = items,
                    sharePractice = sharePractice,
                    isSelectionModeEnabled = isSelectionModeEnabled,
                    selectedItems = previousState.let { it as? ScreenState.Loaded.Groups }
                        ?.let {
                            it.selectedItems.intersect(
                                other = groupsCreationResult.groups.map { it.index }.toSet()
                            )
                        }
                        ?: emptySet(),
                    kanaGroupsMode = groupsCreationResult.kanaGroups,
                    groups = groupsCreationResult.groups
                )
            }
        }
    }

    private suspend fun getRepositoryConfiguration(): PracticePreviewScreenConfiguration {
        val default = PracticePreviewScreenConfiguration(
            layout = PracticePreviewLayout.Groups
        )
        return userPreferencesRepository.run {
            PracticePreviewScreenConfiguration(
                practiceType = getPracticeType()?.toScreenType() ?: default.practiceType,
                filterOption = getFilterOption()?.toScreenType() ?: default.filterOption,
                sortOption = getSortOption()?.toScreenType() ?: default.sortOption,
                isDescending = getIsSortDescending() ?: default.isDescending,
                layout = getPracticePreviewLayout()?.toScreenType() ?: default.layout,
                kanaGroups = getKanaGroupsEnabled()
            )
        }
    }

}