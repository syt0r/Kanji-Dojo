package ua.syt0r.kanji.presentation.screen.screen.practice_preview

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PreviewCharacterData
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SelectionConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SelectionOption
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SortConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeMode
import javax.inject.Inject

@HiltViewModel
class PracticePreviewViewModel @Inject constructor(
    private val userPreferencesRepository: UserDataContract.PreferencesRepository,
    private val fetchListUseCase: PracticePreviewScreenContract.FetchListUseCase,
    private val sortListUseCase: PracticePreviewScreenContract.SortListUseCase
) : ViewModel(), PracticePreviewScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    private lateinit var selectionConfiguration: SelectionConfiguration
    private var sortConfiguration: SortConfiguration = SortConfiguration.default

    override fun loadPracticeInfo(practiceId: Long) {
        viewModelScope.launch {
            state.value = ScreenState.Loading

            val characterList = withContext(Dispatchers.IO) {
                val list = fetchListUseCase.fetch(practiceId)
                sortListUseCase.sort(sortConfiguration, list)
            }

            selectionConfiguration = withContext(Dispatchers.IO) {
                userPreferencesRepository.getSelectionConfiguration()
                    ?: SelectionConfiguration.default
            }

            state.value = ScreenState.Loaded(
                practiceId = practiceId,
                selectionConfiguration = selectionConfiguration,
                sortConfiguration = sortConfiguration,
                characterData = characterList,
                selectedCharacters = getSelectedCharacters(
                    characterList,
                    selectionConfiguration,
                    null
                )
            )
        }
    }

    override fun applySelectionConfig(configuration: SelectionConfiguration) {
        selectionConfiguration = configuration
        val currentState = state.value as ScreenState.Loaded
        state.value = currentState.copy(
            selectionConfiguration = configuration,
            selectedCharacters = getSelectedCharacters(
                characterList = currentState.characterData,
                configuration = configuration,
                previousState = currentState
            )
        )
        viewModelScope.launch {
            userPreferencesRepository.setSelectionConfiguration(configuration)
        }
    }

    override fun applySortConfig(configuration: SortConfiguration) {
        Logger.d("configuration[$configuration]")
        sortConfiguration = configuration
        val currentState = state.value as ScreenState.Loaded
        val sortedCharacters = sortListUseCase.sort(sortConfiguration, currentState.characterData)
        state.value = currentState.copy(
            characterData = sortedCharacters,
            sortConfiguration = configuration,
            selectedCharacters = getSelectedCharacters(
                characterList = sortedCharacters,
                configuration = currentState.selectionConfiguration,
                previousState = currentState
            )
        )
    }

    override fun toggleSelection(characterData: PreviewCharacterData) {
        val screenState = state.value as ScreenState.Loaded

        val selectionConfiguration = screenState.selectionConfiguration
        val updatedSelectionConfiguration = when (selectionConfiguration.option) {
            SelectionOption.ManualSelection -> selectionConfiguration
            else -> selectionConfiguration.copy(option = SelectionOption.ManualSelection)
        }

        val selectedCharacters = screenState.selectedCharacters
        val updatedSelectedCharacters = if (selectedCharacters.contains(characterData.character)) {
            selectedCharacters.minus(characterData.character)
        } else {
            selectedCharacters.plus(characterData.character)
        }

        state.value = screenState.copy(
            selectionConfiguration = updatedSelectionConfiguration,
            selectedCharacters = updatedSelectedCharacters
        )
    }

    override fun clearSelection() {
        val screenState = state.value as ScreenState.Loaded
        state.value = screenState.copy(selectedCharacters = emptySet())
    }

    override fun getPracticeConfiguration(): WritingPracticeConfiguration {
        val screenState = state.value as ScreenState.Loaded
        val practiceCharacters = screenState.characterData
            .filter { screenState.selectedCharacters.contains(it.character) }
            .map { it.character }
            .run { if (screenState.selectionConfiguration.shuffle) shuffled() else this }

        return WritingPracticeConfiguration(
            practiceId = screenState.practiceId,
            characterList = practiceCharacters,
            practiceMode = selectionConfiguration.practiceMode as WritingPracticeMode
        )
    }

    private fun getSelectedCharacters(
        characterList: List<PreviewCharacterData>,
        configuration: SelectionConfiguration,
        previousState: ScreenState.Loaded?
    ): Set<String> {
        return when (configuration.option) {
            SelectionOption.FirstItems -> {
                val count = configuration.firstItemsText.toIntOrNull() ?: 0
                characterList.take(count).map { it.character }.toSet()
            }
            SelectionOption.All -> {
                characterList.map { it.character }.toSet()
            }
            SelectionOption.ManualSelection -> {
                previousState?.selectedCharacters ?: emptySet()
            }
        }
    }

}