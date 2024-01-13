package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji.core.app_data.data.CharacterRadical
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.japanese.CharacterClassification
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeProgress

data class WritingReviewData(
    val progress: PracticeProgress,
    val characterData: WritingReviewCharacterDetails,
    val isStudyMode: Boolean,
    val drawnStrokesCount: MutableState<Int>,
    val currentStrokeMistakes: MutableState<Int>,
    val currentCharacterMistakes: MutableState<Int>
)

sealed class WritingReviewCharacterDetails {

    abstract val character: String
    abstract val strokes: List<Path>
    abstract val radicals: List<CharacterRadical>
    abstract val words: List<JapaneseWord>
    abstract val encodedWords: List<JapaneseWord>

    data class KanaReviewDetails(
        override val character: String,
        override val strokes: List<Path>,
        override val radicals: List<CharacterRadical>,
        override val words: List<JapaneseWord>,
        override val encodedWords: List<JapaneseWord>,
        val kanaSystem: CharacterClassification.Kana,
        val romaji: String
    ) : WritingReviewCharacterDetails()

    data class KanjiReviewDetails(
        override val character: String,
        override val strokes: List<Path>,
        override val radicals: List<CharacterRadical>,
        override val words: List<JapaneseWord>,
        override val encodedWords: List<JapaneseWord>,
        val on: List<String>,
        val kun: List<String>,
        val meanings: List<String>,
    ) : WritingReviewCharacterDetails()

}

data class WritingReviewCharacterSummaryDetails(
    val strokesCount: Int,
    val isStudy: Boolean
)