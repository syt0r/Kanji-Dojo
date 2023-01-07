package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.use_case

import kotlinx.coroutines.delay
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.kanji_data.data.FuriganaAnnotatedCharacter
import ua.syt0r.kanji.core.kanji_data.data.FuriganaString
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.common.ui.kanji.parseKanjiStrokes
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewCharacterData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration
import ua.syt0r.kanji.common.*
import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class LoadWritingPracticeDataUseCase @Inject constructor(
    private val kanjiRepository: KanjiDataRepository
) : WritingPracticeScreenContract.LoadWritingPracticeDataUseCase {

    companion object {
        private const val MINIMAL_LOADING_TIME = 600L //TODO replace with animation delay
        private const val ENCODED_SYMBOL = "○"
    }

    override suspend fun load(
        configuration: WritingPracticeConfiguration
    ): List<ReviewCharacterData> {

        val loadingStartTime = System.currentTimeMillis()

        val kanjiDataList = configuration.characterList.map { character ->
            val strokes = parseKanjiStrokes(kanjiRepository.getStrokes(character))
            val words = kanjiRepository.getWordsWithCharacter(character)
            val encodedWords = encodeWords(character, words)
            when {
                character.first().isKana() -> {
                    val isHiragana = character.first().isHiragana()
                    ReviewCharacterData.KanaReviewData(
                        character = character,
                        strokes = strokes,
                        words = words,
                        encodedWords = encodedWords,
                        kanaSystem = if (isHiragana) CharactersClassification.Kana.HIRAGANA
                        else CharactersClassification.Kana.KATAKANA,
                        romaji = if (isHiragana) hiraganaToRomaji(character.first())
                        else katakanaToRomaji(character.first())
                    )
                }
                else -> {
                    val readings = kanjiRepository.getReadings(character)
                    ReviewCharacterData.KanjiReviewData(
                        character = character,
                        strokes = strokes,
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

        val timeToMinimalLoadingLeft = MINIMAL_LOADING_TIME - System.currentTimeMillis() +
                loadingStartTime
        val delayTime = max(0, min(MINIMAL_LOADING_TIME, timeToMinimalLoadingLeft))

        delay(delayTime)

        return kanjiDataList
    }

    private fun encodeWords(character: String, words: List<JapaneseWord>): List<JapaneseWord> {
        return words.map { word ->
            word.copy(
                furiganaString = FuriganaString(
                    compounds = word.furiganaString.compounds.map {
                        FuriganaAnnotatedCharacter(
                            character = it.character.replace(character, ENCODED_SYMBOL),
                            annotation = it.annotation?.replace(character, ENCODED_SYMBOL)
                        )
                    }
                )
            )
        }
    }


}