package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.runtime.State
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
            val isStudyMode: Boolean,
            val progress: PracticeProgress,
            val drawnStrokesCount: Int = 0,
            val mistakes: Int = 0
        ) : ScreenState()

        sealed class Summary : ScreenState() {

            object Saving : Summary()

            data class Saved(
                val reviewResultList: List<ReviewResult>
            ) : Summary()

        }

    }

}