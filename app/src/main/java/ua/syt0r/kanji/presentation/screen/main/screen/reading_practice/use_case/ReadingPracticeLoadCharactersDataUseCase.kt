package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.use_case

import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema
import ua.syt0r.kanji.common.getKanaClassification
import ua.syt0r.kanji.common.getKanaReading
import ua.syt0r.kanji.common.isKana
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingReviewCharacterData
import javax.inject.Inject

class ReadingPracticeLoadCharactersDataUseCase @Inject constructor(
    private val kanjiDataRepository: KanjiDataRepository,
) : ReadingPracticeContract.LoadCharactersDataUseCase {

    override fun load(
        configuration: MainDestination.Practice.Reading
    ): List<ReadingReviewCharacterData> {
        return configuration.characterList.map { character ->
            val char = character.first()
            when {
                char.isKana() -> {
                    ReadingReviewCharacterData.Kana(
                        reading = getKanaReading(char),
                        classification = getKanaClassification(char),
                        character = character,
                        words = kanjiDataRepository.getKanaWords(character, 5)
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
                        words = kanjiDataRepository.getWordsWithText(character, 5)
                    )
                }
            }
        }
    }

}
