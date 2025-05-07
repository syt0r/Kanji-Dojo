package ua.syt0r.kanji.presentation.screen.main.screen.text_analysis

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.StateFlow
import ua.syt0r.kanji.presentation.common.Paginateable

interface TextAnalysisContract {

    companion object {
        const val INPUT_LIMIT = 100
    }

    sealed interface ScreenState {

        object Loading : ScreenState

        data class Loaded(
            val contentState: StateFlow<TextAnalysisContentState>,
            val inputState: StateFlow<TextAnalysisInputState>,
            val history: StateFlow<Paginateable<TextAnalysisResult.Success>>,
            val setContent: (TextAnalysisResult.Success) -> Unit,
        ) : ScreenState

    }

}

sealed interface TextAnalysisInputState {

    data class Typing(
        val input: MutableState<String>,
        val isInputValid: State<Boolean>,
        val submit: () -> Unit
    ) : TextAnalysisInputState

    data class Loading(
        val input: String
    ) : TextAnalysisInputState

}

sealed interface TextAnalysisContentState {

    object Empty : TextAnalysisContentState

    data class Loaded(
        val contentMode: State<TextAnalysisContentMode>,
        val result: TextAnalysisResult,
    ) : TextAnalysisContentState

}

sealed interface TextAnalysisResult {

    data class Success(
        val text: String,
        val translation: String,
        val nodeList: List<TextAnalysisNode>
    ) : TextAnalysisResult

    data class Error(
        val message: String
    ) : TextAnalysisResult

}

sealed interface TextAnalysisContentMode {

    data class Browse(
        val furigana: MutableState<Boolean>,
        val highlight: MutableState<Boolean>,
        val switchToSaveWordsMode: () -> Unit
    ) : TextAnalysisContentMode

    data class SaveWords(
        val selected: State<Set<TextAnalysisNode.Word>>,
        val toggleSelection: (TextAnalysisNode.Word) -> Unit,
        val selectAll: () -> Unit,
        val selectNone: () -> Unit,
        val switchToBrowseMode: () -> Unit
    ) : TextAnalysisContentMode

}
