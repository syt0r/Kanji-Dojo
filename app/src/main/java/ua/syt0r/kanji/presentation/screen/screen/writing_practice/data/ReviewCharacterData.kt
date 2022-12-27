package ua.syt0r.kanji.presentation.screen.screen.writing_practice.data

import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.common.CharactersClassification

sealed class ReviewCharacterData {

    abstract val character: String
    abstract val strokes: List<Path>
    abstract val words: List<JapaneseWord>
    abstract val encodedWords: List<JapaneseWord>

    data class KanaReviewData(
        override val character: String,
        override val strokes: List<Path>,
        override val words: List<JapaneseWord>,
        override val encodedWords: List<JapaneseWord>,
        val kanaSystem: CharactersClassification.Kana,
        val romaji: String
    ) : ReviewCharacterData()

    data class KanjiReviewData(
        override val character: String,
        override val strokes: List<Path>,
        override val words: List<JapaneseWord>,
        override val encodedWords: List<JapaneseWord>,
        val on: List<String>,
        val kun: List<String>,
        val meanings: List<String>,
    ) : ReviewCharacterData()

}