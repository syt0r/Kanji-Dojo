package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.*

interface WritingPracticeScreenContract {

    interface ScreenContent {

        @Composable
        fun Draw(
            configuration: WritingPracticeConfiguration,
            mainNavigationState: MainNavigationState,
        )

    }

    interface ViewModel {

        val state: State<ScreenState>

        fun init(practiceConfiguration: WritingPracticeConfiguration)
        suspend fun submitUserDrawnPath(drawData: DrawData): DrawResult

        fun handleCorrectlyDrawnStroke()
        fun handleIncorrectlyDrawnStroke()

        fun loadNextCharacter(userAction: ReviewUserAction)

        fun toggleRadicalsHighlight()

    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Review(
            val data: ReviewCharacterData,
            val isStudyMode: Boolean,
            val progress: PracticeProgress,
            val shouldHighlightRadicals: Boolean,
            val drawnStrokesCount: Int = 0,
            val currentStrokeMistakes: Int = 0,
            val currentCharacterMistakes: Int = 0
        ) : ScreenState()

        sealed class Summary : ScreenState() {

            object Saving : Summary()

            data class Saved(
                val reviewResultList: List<ReviewResult>,
                val eligibleForInAppReview: Boolean
            ) : Summary()

        }

    }

    interface LoadWritingPracticeDataUseCase {
        suspend fun load(configuration: WritingPracticeConfiguration): List<ReviewCharacterData>
    }

    interface IsEligibleForInAppReviewUseCase {
        suspend fun check(): Boolean
    }

}