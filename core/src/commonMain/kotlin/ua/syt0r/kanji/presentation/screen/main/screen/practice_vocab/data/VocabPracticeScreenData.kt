package ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import kotlinx.coroutines.Deferred
import kotlinx.serialization.Serializable
import ua.syt0r.kanji.core.app_data.data.FuriganaString
import ua.syt0r.kanji.core.srs.SrsCardKey
import ua.syt0r.kanji.presentation.common.ScreenVocabPracticeType
import ua.syt0r.kanji.presentation.screen.main.screen.info.InfoScreenData
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.CharacterWriterState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeAnswers
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeConfigurationCard
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeQueueProgress
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeSummaryItem
import kotlin.time.Duration

@Serializable
data class VocabPracticeScreenConfiguration(
    val cards: List<Card>,
    val practiceType: ScreenVocabPracticeType
) {

    @Serializable
    data class Card(
        val cardId: Long,
        val deckId: Long
    ) : PracticeConfigurationCard

}

data class VocabPracticeCardConfigurationData(
    val cardId: Long,
    val deckId: Long,
    val srsCardKey: SrsCardKey,
    val isNew: Boolean
)

sealed interface VocabPracticeConfiguration {

    data class Flashcard(
        val translationInFront: MutableState<Boolean>
    ) : VocabPracticeConfiguration

    data class ReadingPicker(
        val showMeaning: MutableState<Boolean>
    ) : VocabPracticeConfiguration

    data class Writing(
        val showKanaReading: MutableState<Boolean>
    ) : VocabPracticeConfiguration

}

sealed interface VocabReviewState {

    val meaning: String
    val vocabReference: InfoScreenData.Vocab

    interface Flashcard : VocabReviewState {
        val reading: FuriganaString
        val noFuriganaReading: FuriganaString
        val exampleSentence: VocabExampleSentence?
        val showMeaningInFront: Boolean
        val showAnswer: State<Boolean>
    }

    interface Reading : VocabReviewState {
        val questionCharacter: String
        val showMeaning: Boolean
        val displayReading: State<FuriganaString>
        val answers: List<String>
        val correctAnswer: String
        val selectedAnswer: State<SelectedReadingAnswer?>
    }

    interface Writing : VocabReviewState {
        val charactersData: List<VocabCharacterWritingData>
        val selected: MutableState<VocabCharacterWritingData>
        val kanaReading: String
        val showKanaReading: Boolean
    }

}

sealed interface VocabCharacterWritingData {

    val character: String

    data class NoStrokes(
        override val character: String
    ) : VocabCharacterWritingData

    data class WithStrokes(
        override val character: String,
        val writerState: CharacterWriterState
    ) : VocabCharacterWritingData

}

data class SelectedReadingAnswer(
    val selected: String,
    val correct: String
) {
    val isCorrect = selected == correct
}

data class VocabExampleSentence(
    val text: String,
    val furigana: FuriganaString,
    val translation: String
)

data class VocabPracticeReviewState(
    val progress: PracticeQueueProgress,
    val reviewState: VocabReviewState,
    val answers: PracticeAnswers
)

data class VocabSummaryItem(
    val reading: FuriganaString,
    val vocabReference: InfoScreenData.Vocab,
    override val totalReviews: Deferred<Int>,
    override val nextInterval: Duration
) : PracticeSummaryItem
