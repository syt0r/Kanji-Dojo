package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.use_case

import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract

class KanjiInfoLoadCharacterWordsUseCase(
    private val kanjiDataRepository: KanjiDataRepository
) : KanjiInfoScreenContract.LoadCharacterWordsUseCase {

    override suspend fun load(character: String, offset: Int, limit: Int): List<JapaneseWord> {
        return kanjiDataRepository.getWordsWithText(character, offset, limit)
    }

}