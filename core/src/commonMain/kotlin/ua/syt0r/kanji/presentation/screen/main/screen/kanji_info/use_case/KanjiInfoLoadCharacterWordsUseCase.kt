package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.use_case

import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract

class KanjiInfoLoadCharacterWordsUseCase(
    private val appDataRepository: AppDataRepository
) : KanjiInfoScreenContract.LoadCharacterWordsUseCase {

    override suspend fun load(character: String, offset: Int, limit: Int): List<JapaneseWord> {
        return appDataRepository.getWordsWithText(character, offset, limit)
    }

}