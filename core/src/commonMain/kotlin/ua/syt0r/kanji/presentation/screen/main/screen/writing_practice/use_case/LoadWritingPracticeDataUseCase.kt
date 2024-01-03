package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.use_case

import ua.syt0r.kanji.core.kanji_data.schema.KanjiReadingTableSchema
import ua.syt0r.kanji.core.japanese.CharactersClassification
import ua.syt0r.kanji.core.japanese.hiraganaToRomaji
import ua.syt0r.kanji.core.japanese.isHiragana
import ua.syt0r.kanji.core.japanese.isKana
import ua.syt0r.kanji.core.japanese.katakanaToRomaji
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.core.kanji_data.data.encodeKanji
import ua.syt0r.kanji.presentation.common.ui.kanji.parseKanjiStrokes
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewCharacterData

class LoadWritingPracticeDataUseCase(
    private val kanjiRepository: KanjiDataRepository
) : WritingPracticeScreenContract.LoadWritingPracticeDataUseCase {

    override suspend fun load(character: String): ReviewCharacterData {
        val strokes = parseKanjiStrokes(kanjiRepository.getStrokes(character))
        return when {
            character.first().isKana() -> {
                val words = kanjiRepository.getKanaWords(
                    char = character,
                    limit = WritingPracticeScreenContract.WordsLimit + 1
                )
                val encodedWords = encodeWords(character, words)
                val isHiragana = character.first().isHiragana()
                ReviewCharacterData.KanaReviewData(
                    character = character,
                    strokes = strokes,
                    radicals = kanjiRepository.getRadicalsInCharacter(character),
                    words = words,
                    encodedWords = encodedWords,
                    kanaSystem = if (isHiragana) CharactersClassification.Kana.Hiragana
                    else CharactersClassification.Kana.Katakana,
                    romaji = if (isHiragana) hiraganaToRomaji(character.first())
                    else katakanaToRomaji(character.first())
                )
            }
            else -> {
                val words = kanjiRepository.getWordsWithText(
                    text = character,
                    limit = WritingPracticeScreenContract.WordsLimit + 1
                )
                val encodedWords = encodeWords(character, words)
                val readings = kanjiRepository.getReadings(character)
                ReviewCharacterData.KanjiReviewData(
                    character = character,
                    strokes = strokes,
                    radicals = kanjiRepository.getRadicalsInCharacter(character),
                    words = words,
                    encodedWords = encodedWords,
                    on = readings.filter { it.value == KanjiReadingTableSchema.ReadingType.ON }
                        .keys
                        .toList(),
                    kun = readings.filter { it.value == KanjiReadingTableSchema.ReadingType.KUN }
                        .keys
                        .toList(),
                    meanings = kanjiRepository.getMeanings(character)
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