package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.MultiselectPracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeGroup
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticePreviewScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeType

class PracticePreviewViewModel(
    private val viewModelScope: CoroutineScope,
    private val reloadDataUseCase: PracticePreviewScreenContract.ReloadDataUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val filterGroupItemsUseCase: PracticePreviewScreenContract.FilterGroupItemsUseCase,
    private val sortGroupItemsUseCase: PracticePreviewScreenContract.SortGroupItemsUseCase,
    private val createGroupsUseCase: PracticePreviewScreenContract.CreatePracticeGroupsUseCase,
    private val analyticsManager: AnalyticsManager
) : PracticePreviewScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    private var practiceId: Long = -1

    override fun updateScreenData(practiceId: Long) {
        this.practiceId = practiceId
        val previousLoadedState = state.value as? ScreenState.Loaded

        viewModelScope.launch {
            state.value = ScreenState.Loading
            state.value = withContext(Dispatchers.IO) {
                reloadDataUseCase.load(practiceId, previousLoadedState)
            }
        }
    }

    override fun updateConfiguration(configuration: PracticePreviewScreenConfiguration) {
        val currentState = state.value
        if (currentState !is ScreenState.Loaded || currentState.configuration == configuration) {
            return
        }

        viewModelScope.launch {
            state.value = ScreenState.Loading

            state.value = withContext(Dispatchers.IO) {

                userPreferencesRepository.apply {
                    setPracticeType(configuration.practiceType.correspondingRepoType)
                    setFilterOption(configuration.filterOption.correspondingRepoType)
                    setSortOption(configuration.sortOption.correspondingRepoType)
                    setIsSortDescending(configuration.isDescending)
                }

                val groups = filterGroupItemsUseCase
                    .filter(
                        items = currentState.items,
                        practiceType = configuration.practiceType,
                        filterOption = configuration.filterOption
                    )
                    .let {
                        sortGroupItemsUseCase.sort(
                            items = it,
                            sortOption = configuration.sortOption,
                            isDescending = configuration.isDescending
                        )
                    }
                    .let {
                        createGroupsUseCase.create(it, configuration.practiceType)
                    }

                currentState.copy(
                    configuration = configuration,
                    groups = groups,
                    selectedGroupIndexes = currentState.selectedGroupIndexes
                        .intersect(other = groups.map { it.index }.toSet())
                )

            }
        }

    }

    override fun toggleMultiSelectMode() {
        val currentState = state.value as ScreenState.Loaded
        state.value = currentState.run {
            copy(
                isMultiselectEnabled = !isMultiselectEnabled,
                selectedGroupIndexes = emptySet()
            )
        }
    }

    override fun toggleSelectionForGroup(group: PracticeGroup) {
        val currentState = state.value as ScreenState.Loaded
        val index = group.index
        state.value = currentState.run {
            copy(
                selectedGroupIndexes = if (selectedGroupIndexes.contains(index)) {
                    selectedGroupIndexes.minus(index)
                } else {
                    selectedGroupIndexes.plus(index)
                }
            )
        }
    }

    override fun selectAll() {
        val currentState = state.value as ScreenState.Loaded
        state.value = currentState.run {
            copy(selectedGroupIndexes = groups.map { it.index }.toSet())
        }
    }

    override fun deselectAll() {
        val currentState = state.value as ScreenState.Loaded
        state.value = currentState.copy(selectedGroupIndexes = emptySet())
    }

    override fun getPracticeConfiguration(practiceGroup: PracticeGroup): MainDestination.Practice {
        val currentState = state.value as ScreenState.Loaded
        val characters = practiceGroup.items.map { it.character }

        return when (currentState.configuration.practiceType) {
            PracticeType.Writing -> MainDestination.Practice.Writing(
                practiceId = practiceId,
                characterList = characters
            )

            PracticeType.Reading -> MainDestination.Practice.Reading(
                practiceId = practiceId,
                characterList = characters
            )
        }
    }

    override fun getPracticeConfiguration(
        configuration: MultiselectPracticeConfiguration
    ): MainDestination.Practice {
        val practiceType = state.value.let { it as ScreenState.Loaded }
            .configuration
            .practiceType

        val characters = configuration.groups
            .filter { configuration.selectedGroupIndexes.contains(it.index) }
            .flatMap { it.items }
            .map { it.character }
            .shuffled()
            .take(configuration.selectedItemsCount)
            .toList()

        return when (practiceType) {
            PracticeType.Writing -> MainDestination.Practice.Writing(
                practiceId = practiceId,
                characterList = characters
            )

            PracticeType.Reading -> MainDestination.Practice.Reading(
                practiceId = practiceId,
                characterList = characters
            )
        }
    }

    override fun reportScreenShown() {
        analyticsManager.setScreen("practice_preview")
    }

}