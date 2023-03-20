package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.use_case

import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema
import ua.syt0r.kanji.common.getKanaClassification
import ua.syt0r.kanji.common.getKanaReading
import ua.syt0r.kanji.common.isKana
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingReviewCharacterData

class ReadingPracticeLoadCharactersDataUseCase(
    private val kanjiDataRepository: KanjiDataRepository,
) : ReadingPracticeContract.LoadCharactersDataUseCase {

    override suspend fun load(character: String): ReadingReviewCharacterData {
        val char = character.first()
        return when {
            char.isKana() -> {
                ReadingReviewCharacterData.Kana(
                    reading = getKanaReading(char),
                    classification = getKanaClassification(char),
                    character = character,
                    words = kanjiDataRepository.getKanaWords(
                        char = character,
                        limit = ReadingPracticeContract.DisplayWordsLimit
                    )
                )
            }
            else -> {
                val readings = kanjiDataRepository.getReadings(character)
                ReadingReviewCharacterData.Kanji(
                    character = character,
                    on = readings
                        .filter { it.value == KanjiReadingTableSchema.ReadingType.ON }
                        .map { it.key },
                    kun = readings
                        .filter { it.value == KanjiReadingTableSchema.ReadingType.KUN }
                        .map { it.key },
                    meanings = kanjiDataRepository.getMeanings(character),
                    words = kanjiDataRepository.getWordsWithText(
                        text = character,
                        limit = ReadingPracticeContract.DisplayWordsLimit
                    )
                )
            }
        }
    }

}
