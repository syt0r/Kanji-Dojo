package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search

import androidx.compose.runtime.State
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord

interface SearchScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun search(input: String)

        fun reportScreenShown()

    }

    data class ScreenState(
        val isLoading: Boolean,
        val characters: List<String> = emptyList(),
        val words: List<JapaneseWord> = emptyList()
    )

    interface ProcessInputUseCase {
        suspend fun process(input: String): ScreenState
    }

}