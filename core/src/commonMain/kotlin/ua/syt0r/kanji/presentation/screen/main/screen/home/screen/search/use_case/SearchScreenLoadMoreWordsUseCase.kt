package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.use_case

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreenContract

class SearchScreenLoadMoreWordsUseCase(
    private val kanjiDataRepository: KanjiDataRepository
) : SearchScreenContract.LoadMoreWordsUseCase {

    override suspend fun loadMore(state: SearchScreenContract.ScreenState) {
        val wordsState = state.words as MutableState
        val currentWords = wordsState.value
        wordsState.value = withContext(Dispatchers.IO) {
            val newItems = currentWords.items + kanjiDataRepository.getWordsWithText(
                text = state.query,
                offset = currentWords.items.size,
                limit = SearchScreenContract.LoadMoreWordsCount
            )
            currentWords.copy(items = newItems)
        }
    }

}