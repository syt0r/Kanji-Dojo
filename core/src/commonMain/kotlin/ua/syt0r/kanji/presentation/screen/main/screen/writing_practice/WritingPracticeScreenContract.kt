package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import ua.syt0r.kanji.core.user_data.model.CharacterReviewOutcome
import ua.syt0r.kanji.core.user_data.model.OutcomeSelectionConfiguration
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewCharacterData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewUserAction
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.StrokeInputData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.StrokeProcessingResult
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeCharReviewResult
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingScreenConfiguration
import kotlin.time.Duration

interface WritingPracticeScreenContract {

    companion object {
        const val WordsLimit = 100
    }

    interface Content {

        @Composable
        fun Draw(
            configuration: MainDestination.Practice.Writing,
            mainNavigationState: MainNavigationState,
            viewModel: ViewModel
        )

    }

    interface ViewModel {

        val state: State<ScreenState>

        fun init(configuration: MainDestination.Practice.Writing)
        fun onPracticeConfigured(configuration: WritingScreenConfiguration)
        suspend fun submitUserDrawnPath(inputData: StrokeInputData): StrokeProcessingResult
        fun loadNextCharacter(userAction: ReviewUserAction)
        fun savePractice(
            outcomeSelectionConfiguration: OutcomeSelectionConfiguration,
            outcomes: Map<String, CharacterReviewOutcome>
        )

        fun onHintClick()
        fun toggleRadicalsHighlight()

        fun reportScreenShown(configuration: MainDestination.Practice.Writing)

    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Configuring(
            val characters: List<String>,
            val configuration: WritingScreenConfiguration
        ) : ScreenState()

        data class Review(
            val shouldHighlightRadicals: State<Boolean>,
            val configuration: WritingScreenConfiguration,
            val reviewState: State<WritingReviewData>
        ) : ScreenState()

        data class Saving(
            val outcomeSelectionConfiguration: OutcomeSelectionConfiguration,
            val reviewResultList: List<WritingPracticeCharReviewResult>
        ) : ScreenState()

        data class Saved(
            val practiceDuration: Duration,
            val accuracy: Float,
            val repeatCharacters: List<String>,
            val goodCharacters: List<String>
        ) : ScreenState()

    }

    interface LoadWritingPracticeDataUseCase {
        suspend fun load(character: String): ReviewCharacterData
    }

    interface IsEligibleForInAppReviewUseCase {
        suspend fun check(): Boolean
    }

}