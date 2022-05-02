package ua.syt0r.kanji.presentation.screen.screen.practice_preview

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PreviewCharacterData
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SelectionConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SelectionOption
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration
import javax.inject.Inject

@HiltViewModel
class PracticePreviewViewModel @Inject constructor(
    private val usedDataRepository: UserDataContract.PracticeRepository
) : ViewModel(), PracticePreviewScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun loadPracticeInfo(practiceId: Long) {
        viewModelScope.launch {
            state.value = ScreenState.Loading
            val characterList = withContext(Dispatchers.IO) {
                usedDataRepository.getKanjiForPracticeSet(practiceId)
                    .map { PreviewCharacterData(it) }
            }
            val configuration = SelectionConfiguration.default
            state.value = ScreenState.Loaded(
                practiceId = practiceId,
                selectionConfig = configuration,
                characterData = characterList,
                selectedCharacters = getSelectedCharacters(characterList, configuration, null)
            )
        }
    }

    override fun submitSelectionConfig(configuration: SelectionConfiguration) {
        val currentState = state.value as ScreenState.Loaded
        state.value = currentState.copy(
            selectionConfig = configuration,
            selectedCharacters = getSelectedCharacters(
                characterList = currentState.characterData,
                configuration = configuration,
                previousState = currentState
            )
        )
    }

    override fun toggleSelection(characterData: PreviewCharacterData) {
        val screenState = state.value as ScreenState.Loaded
        state.value = screenState.copy(
            selectionConfig = screenState.selectionConfig.run {
                if (option != SelectionOption.ManualSelection) {
                    copy(option = SelectionOption.ManualSelection)
                } else this
            },
            selectedCharacters = screenState.run {
                if (selectedCharacters.contains(characterData.character)) {
                    selectedCharacters.minus(characterData.character)
                } else {
                    selectedCharacters.plus(characterData.character)
                }
            }
        )
    }

    override fun clearSelection() {
        val screenState = state.value as ScreenState.Loaded
        state.value = screenState.copy(
            selectedCharacters = emptySet()
        )
    }

    override fun getPracticeConfiguration(): WritingPracticeConfiguration {
        val screenState = state.value as ScreenState.Loaded
        return WritingPracticeConfiguration(
            practiceId = screenState.practiceId,
            characterList = screenState.characterData
                .filter { screenState.selectedCharacters.contains(it.character) }
                .map { it.character }
                .run { if (screenState.selectionConfig.shuffle) shuffled() else this }
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
                if (previousState?.selectionConfig?.option == SelectionOption.ManualSelection) {
                    previousState.selectedCharacters
                } else {
                    emptySet()
                }
            }
        }
    }

}