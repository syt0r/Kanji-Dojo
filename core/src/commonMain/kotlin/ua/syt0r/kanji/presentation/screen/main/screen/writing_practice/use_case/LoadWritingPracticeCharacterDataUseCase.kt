package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.use_case

import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.app_data.data.ReadingType
import ua.syt0r.kanji.core.app_data.data.encodeKanji
import ua.syt0r.kanji.core.japanese.RomajiConverter
import ua.syt0r.kanji.core.japanese.getKanaInfo
import ua.syt0r.kanji.core.japanese.getWordWithExtraRomajiReading
import ua.syt0r.kanji.core.japanese.isKana
import ua.syt0r.kanji.presentation.common.ui.kanji.parseKanjiStrokes
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewCharacterDetails

class LoadWritingPracticeCharacterDataUseCase(
    private val kanjiRepository: AppDataRepository,
    private val romajiConverter: RomajiConverter
) : WritingPracticeScreenContract.LoadCharacterDataUseCase {

    override suspend fun load(character: String): WritingReviewCharacterDetails {
        val strokes = parseKanjiStrokes(kanjiRepository.getStrokes(character))
        return when {
            character.first().isKana() -> {
                val kanaInfo = getKanaInfo(character.first())

                val words = kanjiRepository.getKanaWords(
                    char = character,
                    limit = WritingPracticeScreenContract.WordsLimit + 1
                )
                val wordsWithRomajiReading = words.map {
                    romajiConverter.getWordWithExtraRomajiReading(it)
                }

                WritingReviewCharacterDetails.KanaReviewDetails(
                    character = character,
                    strokes = strokes,
                    words = wordsWithRomajiReading,
                    encodedWords = encodeWords(character, wordsWithRomajiReading),
                    kanaSystem = kanaInfo.classification,
                    reading = kanaInfo.reading
                )
            }

            else -> {
                val words = kanjiRepository.getWordsWithText(
                    text = character,
                    limit = WritingPracticeScreenContract.WordsLimit + 1
                )
                val encodedWords = encodeWords(character, words)
                val readings = kanjiRepository.getReadings(character)
                WritingReviewCharacterDetails.KanjiReviewDetails(
                    character = character,
                    strokes = strokes,
                    radicals = kanjiRepository.getRadicalsInCharacter(character),
                    words = words,
                    encodedWords = encodedWords,
                    on = readings.filter { it.value == ReadingType.ON }
                        .keys
                        .toList(),
                    kun = readings.filter { it.value == ReadingType.KUN }
                        .keys
                        .toList(),
                    meanings = kanjiRepository.getMeanings(character),
                    variants = kanjiRepository.getData(character)
                        ?.variantFamily
                        ?.replace(character, "")
                )
            }
        }
    }

    private fun encodeWords(character: String, words: List<JapaneseWord>): List<JapaneseWord> {
        return words.map { word ->
            word.copy(readings = word.readings.map { it.encodeKanji(character) })
        }
    }

}