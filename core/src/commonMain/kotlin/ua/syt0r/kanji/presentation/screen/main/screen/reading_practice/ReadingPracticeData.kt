package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import androidx.compose.runtime.State
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.japanese.CharacterClassification
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeProgress

enum class ReadingPracticeSelectedOption { RevealAnswer, Repeat, Good }

data class ReadingPracticeCharacterSummaryDetails(val repeats: Int)

sealed interface ReadingReviewCharacterData {

    val character: String
    val words: List<JapaneseWord>

    data class Kana(
        override val character: String,
        override val words: List<JapaneseWord>,
        val reading: String,
        val classification: CharacterClassification.Kana,
    ) : ReadingReviewCharacterData

    data class Kanji(
        override val character: String,
        override val words: List<JapaneseWord>,
        val on: List<String>,
        val kun: List<String>,
        val meanings: List<String>
    ) : ReadingReviewCharacterData

}

data class ReadingReviewData(
    val progress: PracticeProgress,
    val characterData: ReadingReviewCharacterData,
    val showAnswer: State<Boolean>,
    val kanaVoiceAutoPlay: State<Boolean>
)

data class ReadingScreenConfiguration(
    val characters: List<String>,
    val shuffle: Boolean
)