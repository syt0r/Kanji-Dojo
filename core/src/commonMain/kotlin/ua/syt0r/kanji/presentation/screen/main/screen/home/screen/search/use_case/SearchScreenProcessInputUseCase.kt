package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.use_case

import androidx.compose.runtime.mutableStateOf
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.japanese.isKana
import ua.syt0r.kanji.core.japanese.isKanji
import ua.syt0r.kanji.presentation.common.PaginatableJapaneseWordList
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreenContract

class SearchScreenProcessInputUseCase(
    private val appDataRepository: AppDataRepository
) : SearchScreenContract.ProcessInputUseCase {

    override suspend fun process(
        input: String
    ): SearchScreenContract.ScreenState {

        val knownCharacters = input.mapNotNull {
            val charString = it.toString()
            val areStrokesAvailable = appDataRepository.getStrokes(charString).isNotEmpty()

            val isKnown = areStrokesAvailable && when {
                it.isKana() -> true
                it.isKanji() -> {
                    appDataRepository.getReadings(charString).isNotEmpty()
                }

                else -> false
            }

            if (isKnown) charString else null
        }


        val (wordsCount, words) = input.takeIf { it.isNotEmpty() }
            ?.let {
                val wordsCount = appDataRepository.getWordsWithTextCount(input)
                val words = appDataRepository.getWordsWithText(
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