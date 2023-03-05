package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data

import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord

sealed interface ReadingReviewCharacterData {

    val character: String
    val words: List<JapaneseWord>

    data class Kana(
        override val character: String,
        override val words: List<JapaneseWord>,
        val reading: String,
        val classification: CharactersClassification.Kana,
    ) : ReadingReviewCharacterData

    data class Kanji(
        override val character: String,
        override val words: List<JapaneseWord>,
        val on: List<String>,
        val kun: List<String>,
        val meanings: List<String>
    ) : ReadingReviewCharacterData

}
