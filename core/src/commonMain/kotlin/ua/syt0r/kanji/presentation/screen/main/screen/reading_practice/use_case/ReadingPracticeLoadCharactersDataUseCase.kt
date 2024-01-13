package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.use_case

import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.app_data.data.ReadingType
import ua.syt0r.kanji.core.japanese.CharacterClassification.Kana
import ua.syt0r.kanji.core.japanese.getKanaReading
import ua.syt0r.kanji.core.japanese.isHiragana
import ua.syt0r.kanji.core.japanese.isKana
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingReviewCharacterData

class ReadingPracticeLoadCharactersDataUseCase(
    private val appDataRepository: AppDataRepository,
) : ReadingPracticeContract.LoadCharactersDataUseCase {

    override suspend fun load(character: String): ReadingReviewCharacterData {
        val char = character.first()
        return when {
            char.isKana() -> {
                ReadingReviewCharacterData.Kana(
                    reading = getKanaReading(char),
                    classification = if (char.isHiragana()) Kana.Hiragana else Kana.Katakana,
                    character = character,
                    words = appDataRepository.getKanaWords(
                        char = character,
                        limit = ReadingPracticeContract.DisplayWordsLimit
                    )
                )
            }

            else -> {
                val readings = appDataRepository.getReadings(character)
                ReadingReviewCharacterData.Kanji(
                    character = character,
                    on = readings
                        .filter { it.value == ReadingType.ON }
                        .map { it.key },
                    kun = readings
                        .filter { it.value == ReadingType.KUN }
                        .map { it.key },
                    meanings = appDataRepository.getMeanings(character),
                    words = appDataRepository.getWordsWithText(
                        text = character,
                        limit = ReadingPracticeContract.DisplayWordsLimit
                    )
                )
            }
        }
    }

}
