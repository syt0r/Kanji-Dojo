package ua.syt0r.kanji.presentation.screen.screen.practice_import

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.screen.practice_import.data.ImportPracticeCategory

interface PracticeImportScreenContract {

    interface ViewModel {
        val state: State<ScreenState>
    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Loaded(
            val categories: List<ImportPracticeCategory>
        ) : ScreenState()

    }

}

