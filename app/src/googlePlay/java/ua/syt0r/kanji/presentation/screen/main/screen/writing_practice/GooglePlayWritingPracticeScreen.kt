package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.core.review.LocalReviewManager
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.DrawResult
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui.WritingPracticeScreenUI


@Composable
fun GooglePlayWritingPracticeScreen(
    configuration: PracticeScreenConfiguration.Writing,
    mainNavigationState: MainNavigationState,
    viewModel: WritingPracticeScreenContract.ViewModel = hiltViewModel<WritingPracticeViewModel>(),
) {

    LaunchedEffect(Unit) {
        viewModel.init(configuration)
        viewModel.reportScreenShown(configuration)
    }

    val state = viewModel.state

    WritingPracticeScreenUI(
        state = state,
        navigateBack = { mainNavigationState.navigateBack() },
        submitUserInput = { viewModel.submitUserDrawnPath(it) },
        onAnimationCompleted = {
            when (it) {
                is DrawResult.Correct -> viewModel.handleCorrectlyDrawnStroke()
                is DrawResult.Mistake -> viewModel.handleIncorrectlyDrawnStroke()
                DrawResult.IgnoreCompletedPractice -> {}
            }
        },
        onHintClick = { viewModel.handleIncorrectlyDrawnStroke() },
        onReviewItemClick = {
            mainNavigationState.navigateToKanjiInfo(it.characterReviewResult.character)
        },
        onPracticeCompleteButtonClick = { mainNavigationState.navigateBack() },
        onNextClick = { viewModel.loadNextCharacter(it) },
        toggleRadicalsHighlight = { viewModel.toggleRadicalsHighlight() }
    )

    InAppReview(state = state)

}

@Composable
private fun InAppReview(
    state: State<ScreenState>,
) {
    val shouldStartReview by remember {
        derivedStateOf {
            state.value.let { it is ScreenState.Summary.Saved && it.eligibleForInAppReview }
        }
    }

    if (shouldStartReview) {
        LocalReviewManager.current.StartReview()
    }
}