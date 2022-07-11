package ua.syt0r.kanji.presentation.screen.screen.practice_preview

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PreviewCharacterData
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SelectionConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SortConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration

interface PracticePreviewScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun loadPracticeInfo(practiceId: Long)
        fun applySelectionConfig(configuration: SelectionConfiguration)
        fun applySortConfig(configuration: SortConfiguration)

        fun toggleSelection(characterData: PreviewCharacterData)
        fun clearSelection()

        fun getPracticeConfiguration(): WritingPracticeConfiguration

    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Loaded(
            val practiceId: Long,
            val selectionConfiguration: SelectionConfiguration,
            val sortConfiguration: SortConfiguration,
            val characterData: List<PreviewCharacterData>,
            val selectedCharacters: Set<String>
        ) : ScreenState()

    }


    interface FetchListUseCase {
        suspend fun fetch(practiceId: Long): List<PreviewCharacterData>
    }

    interface SortListUseCase {
        fun sort(
            sortConfiguration: SortConfiguration,
            characterList: List<PreviewCharacterData>
        ): List<PreviewCharacterData>
    }

}
