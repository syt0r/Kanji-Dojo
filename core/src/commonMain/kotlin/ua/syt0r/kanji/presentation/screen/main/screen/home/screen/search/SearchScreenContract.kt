package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search

import androidx.compose.runtime.State
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data.RadicalSearchListItem
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data.RadicalSearchState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data.SearchByRadicalsResult

interface SearchScreenContract {

    interface ViewModel {

        val state: State<ScreenState>
        val radicalsState: State<RadicalSearchState>

        fun search(input: String)

        // Added for performance issues, loaded list makes switching to screen junky
        fun loadRadicalsData()
        fun radicalsSearch(radicals: Set<String>)

        fun reportScreenShown()

    }

    data class ScreenState(
        val isLoading: Boolean,
        val characters: List<String> = emptyList(),
        val words: List<JapaneseWord> = emptyList()
    )

    interface ProcessInputUseCase {
        fun process(input: String): ScreenState
    }

    interface LoadRadicalsUseCase {
        suspend fun load(): List<RadicalSearchListItem>
    }

    interface SearchByRadicalsUseCase {
        suspend fun search(radicals: Set<String>): SearchByRadicalsResult
    }

    interface UpdateEnabledRadicalsUseCase {
        suspend fun update(
            allRadicals: List<RadicalSearchListItem>,
            searchResult: SearchByRadicalsResult
        ): List<RadicalSearchListItem>
    }

}