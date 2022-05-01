package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.runtime.State
import ua.syt0r.kanji.core.user_data.model.KanjiWritingReview
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.*

interface WritingPracticeScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun init(practiceConfiguration: WritingPracticeConfiguration)
        suspend fun submitUserDrawnPath(drawData: DrawData): DrawResult

        fun handleCorrectlyDrawnStroke()
        fun handleIncorrectlyDrawnStroke()

    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Review(
            val data: ReviewCharacterData,
            val progress: PracticeProgress,
            val drawnStrokesCount: Int = 0,
            val mistakes: Int = 0
        ) : ScreenState()

        data class Summary(
            val reviewList: List<KanjiWritingReview>
        ) : ScreenState()

    }

}