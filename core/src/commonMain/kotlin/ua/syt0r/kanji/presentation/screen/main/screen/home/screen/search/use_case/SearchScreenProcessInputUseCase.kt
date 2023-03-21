package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.use_case

import androidx.compose.runtime.mutableStateOf
import ua.syt0r.kanji.common.*
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.presentation.common.PaginatableJapaneseWordList
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreenContract

class SearchScreenProcessInputUseCase(
    private val kanjiDataRepository: KanjiDataRepository
) : SearchScreenContract.ProcessInputUseCase {

    override suspend fun process(
        input: String
    ): SearchScreenContract.ScreenState {

        val knownCharacters = input.mapNotNull {
            val charString = it.toString()
            val areStrokesAvailable = kanjiDataRepository.getStrokes(charString).isNotEmpty()

            val isKnown = areStrokesAvailable && when {
                it.isHiragana() -> Hiragana.contains(it)
                it.isKatakana() -> Katakana.contains(it)
                it.isKanji() -> {
                    kanjiDataRepository.getReadings(charString).isNotEmpty() &&
                            kanjiDataRepository.getMeanings(charString).isNotEmpty()
                }
                else -> false
            }

            if (isKnown) charString else null
        }


        val (wordsCount, words) = input.takeIf { it.isNotEmpty() }
            ?.let {
                val wordsCount = kanjiDataRepository.getWordsWithTextCount(input)
                val words = kanjiDataRepository.getWordsWithText(
                    text = it,
                    limit = SearchScreenContract.InitialWordsCount
                )
                wordsCount to words
            }
            ?: (0 to emptyList())

        return SearchScreenContract.ScreenState(
            isLoading = false,
            characters = knownCharacters,
            words = mutableStateOf(PaginatableJapaneseWordList(wordsCount, words)),
            query = input
        )
    }

}