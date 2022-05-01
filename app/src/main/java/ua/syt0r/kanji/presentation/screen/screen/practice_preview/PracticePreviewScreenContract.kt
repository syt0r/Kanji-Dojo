package ua.syt0r.kanji.presentation.screen.screen.practice_preview

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.PreviewCharacterData
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SelectionConfiguration

interface PracticePreviewScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun loadPracticeInfo(practiceId: Long)
        fun submitSelectionConfig(configuration: SelectionConfiguration)

    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Loaded(
            val practiceId: Long,
            val selectionConfig: SelectionConfiguration,
            val characterData: List<PreviewCharacterData>,
            val selectedCharacters: List<String>
        ) : ScreenState()

    }

}
