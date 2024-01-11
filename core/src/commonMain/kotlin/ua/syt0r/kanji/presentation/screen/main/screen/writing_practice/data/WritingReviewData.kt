package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data

import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji.core.japanese.CharacterClassification
import ua.syt0r.kanji.core.app_data.data.CharacterRadical
import ua.syt0r.kanji.core.app_data.data.JapaneseWord

data class WritingReviewData(
    val progress: WritingPracticeProgress,
    val characterData: ReviewCharacterData,
    val isStudyMode: Boolean,
    val drawnStrokesCount: Int = 0,
    val currentStrokeMistakes: Int = 0,
    val currentCharacterMistakes: Int = 0
)

data class WritingPracticeProgress(
    val pendingCount: Int,
    val repeatCount: Int,
    val finishedCount: Int,
    val totalReviews: Int
)

sealed class ReviewCharacterData {

    abstract val character: String
    abstract val strokes: List<Path>
    abstract val radicals: List<CharacterRadical>
    abstract val words: List<JapaneseWord>
    abstract val encodedWords: List<JapaneseWord>

    data class KanaReviewData(
        override val character: String,
        override val strokes: List<Path>,
        override val radicals: List<CharacterRadical>,
        override val words: List<JapaneseWord>,
        override val encodedWords: List<JapaneseWord>,
        val kanaSystem: CharacterClassification.Kana,
        val romaji: String
    ) : ReviewCharacterData()

    data class KanjiReviewData(
        override val character: String,
        override val strokes: List<Path>,
        override val radicals: List<CharacterRadical>,
        override val words: List<JapaneseWord>,
        override val encodedWords: List<JapaneseWord>,
        val on: List<String>,
        val kun: List<String>,
        val meanings: List<String>,
    ) : ReviewCharacterData()

}