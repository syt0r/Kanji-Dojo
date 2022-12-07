package ua.syt0r.kanji.presentation.screen.screen.writing_practice.data

import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji_dojo.shared.CharactersClassification

sealed class ReviewCharacterData {

    abstract val character: String
    abstract val strokes: List<Path>

    data class KanaReviewData(
        override val character: String,
        override val strokes: List<Path>,
        val kanaSystem: CharactersClassification.Kana,
        val romaji: String
    ) : ReviewCharacterData()

    data class KanjiReviewData(
        override val character: String,
        override val strokes: List<Path>,
        val on: List<String>,
        val kun: List<String>,
        val meanings: List<String>,
    ) : ReviewCharacterData()

}