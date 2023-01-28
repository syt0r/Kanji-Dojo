package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.*
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration
import javax.inject.Inject

@HiltViewModel
class PracticePreviewViewModel @Inject constructor(
    private val userPreferencesRepository: UserDataContract.PreferencesRepository,
    private val practiceRepository: UserDataContract.PracticeRepository,
    private val fetchListUseCase: PracticePreviewScreenContract.FetchListUseCase,
    private val sortListUseCase: PracticePreviewScreenContract.SortListUseCase,
    private val createGroupsUseCase: PracticePreviewScreenContract.CreatePracticeGroupsUseCase,
    private val analyticsManager: AnalyticsManager
) : ViewModel(), PracticePreviewScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    private lateinit var sortConfiguration: SortConfiguration

    private var practiceId: Long = -1
    private lateinit var items: List<PracticeGroupItem>

    override fun loadPracticeInfo(practiceId: Long) {
        this.practiceId = practiceId
        val formerLoadedState = state.value as? ScreenState.Loaded

        viewModelScope.launch {
            state.value = ScreenState.Loading

            sortConfiguration = withContext(Dispatchers.IO) {
                userPreferencesRepository.getSortConfiguration() ?: SortConfiguration.default
            }

            val practiceTitle = async(Dispatchers.IO) {
                practiceRepository.getPracticeInfo(practiceId).name
            }

            val characterList = async(Dispatchers.IO) {
                items = fetchListUseCase.fetch(practiceId)
                sortListUseCase.sort(sortConfiguration, items)
                    .let { createGroupsUseCase.create(it) }
            }

            state.value = ScreenState.Loaded(
                title = practiceTitle.await(),
                sortConfiguration = sortConfiguration,
                groups = characterList.await(),
                isMultiselectEnabled = formerLoadedState?.isMultiselectEnabled ?: false,
                selectedGroupIndexes = formerLoadedState?.selectedGroupIndexes
                    ?.let { previouslySelectedGroups ->
                        val newAvailableGroupIndexes = characterList.await()
                            .map { it.index }
                            .toSet()
                        previouslySelectedGroups.intersect(newAvailableGroupIndexes)
                    }
                    ?: emptySet()
            )
        }
    }

    override fun applySortConfig(configuration: SortConfiguration) {
        if (sortConfiguration == configuration) return

        val currentState = state.value
        if (currentState !is ScreenState.Loaded) return

        sortConfiguration = configuration

        viewModelScope.launch {
            state.value = ScreenState.Loading

            withContext(Dispatchers.IO) {

                userPreferencesRepository.setSortConfiguration(configuration)

                state.value = currentState.copy(
                    sortConfiguration = configuration,
                    groups = currentState.groups.flatMap { it.items }
                        .let { sortListUseCase.sort(configuration, it) }
                        .let { createGroupsUseCase.create(it) }
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

    override fun getPracticeConfiguration(
        practiceGroup: PracticeGroup,
        practiceConfiguration: PracticeConfiguration
    ): WritingPracticeConfiguration {
        return WritingPracticeConfiguration(
            practiceId = practiceId,
            characterList = practiceGroup.items
                .map { it.character }
                .let { if (practiceConfiguration.shuffle) it.shuffled() else it },
            isStudyMode = practiceConfiguration.isStudyMode
        )
    }

    override fun getPracticeConfiguration(
        configuration: MultiselectPracticeConfiguration
    ): WritingPracticeConfiguration {
        return WritingPracticeConfiguration(
            practiceId = practiceId,
            characterList = configuration.groups.asSequence()
                .filter { configuration.selectedGroupIndexes.contains(it.index) }
                .flatMap { it.items }
                .map { it.character }
                .shuffled()
                .take(configuration.selectedItemsCount)
                .toList(),
            isStudyMode = false
        )
    }

    override fun reportScreenShown() {
        analyticsManager.setScreen("practice_preview")
    }

}