package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val loadScreenDataUseCase: PracticePreviewScreenContract.LoadScreenDataUseCase,
    private val userPreferencesRepository: UserDataContract.PreferencesRepository,
    private val sortGroupItemsUseCase: PracticePreviewScreenContract.SortGroupItemsUseCase,
    private val createGroupsUseCase: PracticePreviewScreenContract.CreatePracticeGroupsUseCase,
    private val analyticsManager: AnalyticsManager
) : ViewModel(), PracticePreviewScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    private var practiceId: Long = -1

    override fun loadPracticeInfo(practiceId: Long) {
        this.practiceId = practiceId

        val previousLoadedState = state.value as? ScreenState.Loaded

        viewModelScope.launch {
            state.value = ScreenState.Loading
            state.value = withContext(Dispatchers.IO) {
                loadScreenDataUseCase.load(practiceId, previousLoadedState)
            }
        }

    }

    override fun applySortConfig(configuration: SortConfiguration) {
        val currentState = state.value
        if (currentState !is ScreenState.Loaded || currentState.sortConfiguration == configuration) {
            return
        }

        viewModelScope.launch {
            state.value = ScreenState.Loading

            state.value = withContext(Dispatchers.IO) {

                userPreferencesRepository.setSortConfiguration(configuration)

                val listItems = currentState.allGroups.flatMap { it.items }
                    .let { sortGroupItemsUseCase.sort(configuration, it) }

                currentState.copy(
                    sortConfiguration = configuration,
                    allGroups = createGroupsUseCase.create(listItems),
                    reviewOnlyGroups = listItems
                        .filter { it.reviewState == CharacterReviewState.NeedReview }
                        .let { createGroupsUseCase.create(it) }
                )

            }
        }

    }

    override fun applyVisibilityConfig(configuration: VisibilityConfiguration) {
        val currentState = state.value
        if (currentState !is ScreenState.Loaded || currentState.visibilityConfiguration == configuration) {
            return
        }
        state.value = currentState.copy(
            visibilityConfiguration = configuration,
            selectedGroupIndexes = emptySet()
        )
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