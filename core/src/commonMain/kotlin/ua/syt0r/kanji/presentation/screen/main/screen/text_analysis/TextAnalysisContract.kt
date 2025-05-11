package ua.syt0r.kanji.presentation.screen.main.screen.text_analysis

import kotlinx.coroutines.flow.StateFlow
import ua.syt0r.kanji.presentation.common.Paginateable

interface TextAnalysisContract {

    companion object {
        const val INPUT_LIMIT = 100
        const val PREVIEW_RES_PATH = "files/text_analysis_preview.json"
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
