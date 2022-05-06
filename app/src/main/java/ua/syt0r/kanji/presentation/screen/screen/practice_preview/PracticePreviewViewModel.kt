package ua.syt0r.kanji.presentation.screen.screen.practice_preview

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.*
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration
import ua.syt0r.kanji_dojo.shared.isKana
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class PracticePreviewViewModel @Inject constructor(
    private val userDataRepository: UserDataContract.PracticeRepository,
    private val kanjiDataRepository: KanjiDataContract.Repository
) : ViewModel(), PracticePreviewScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    private var selectionConfiguration: SelectionConfiguration = SelectionConfiguration.default
    private var sortConfiguration: SortConfiguration = SortConfiguration.default

    override fun loadPracticeInfo(practiceId: Long) {
        viewModelScope.launch {
            state.value = ScreenState.Loading

            val characterList = withContext(Dispatchers.IO) {
                val characterReviewTimestampsMap = userDataRepository.getCharactersReviewTimestamps(
                    practiceId = practiceId,
                    maxMistakes = 2
                )
                userDataRepository.getKanjiForPracticeSet(practiceId)
                    .map {
                        PreviewCharacterData(
                            character = it,
                            frequency = if (it.first().isKana()) 0
                            else kanjiDataRepository.getData(it)?.frequency ?: Int.MAX_VALUE,
                            lastReviewTime = characterReviewTimestampsMap[it] ?: LocalDateTime.MIN
                        )
                    }
                    .sorted(sortConfiguration)
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
    }

    override fun applySortConfig(configuration: SortConfiguration) {
        sortConfiguration = configuration
        val currentState = state.value as ScreenState.Loaded
        val sortedCharacters = currentState.characterData.sorted(configuration)
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
        state.value = screenState.copy(
            selectionConfiguration = screenState.selectionConfiguration.run {
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
                .run { if (screenState.selectionConfiguration.shuffle) shuffled() else this }
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
                if (previousState?.selectionConfiguration?.option == SelectionOption.ManualSelection) {
                    previousState.selectedCharacters
                } else {
                    emptySet()
                }
            }
        }
    }

    private fun List<PreviewCharacterData>.sorted(
        sortConfiguration: SortConfiguration
    ): List<PreviewCharacterData> {
        return when (sortConfiguration.sortOption) {
            SortOption.NAME -> {
                sortedWith(
                    compareBy { it.character }
                )
            }
            SortOption.FREQUENCY -> {
                sortedWith(
                    compareBy(
                        { it.frequency },
                        { it.character }
                    )
                )
            }
            SortOption.REVIEW_TIME -> {
                sortedWith(
                    compareBy(
                        { it.lastReviewTime },
                        { it.character }
                    )
                )
            }
        }.run { if (sortConfiguration.isDescending) reversed() else this }
    }

}