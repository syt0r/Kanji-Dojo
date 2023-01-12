package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.core.analytics.LocalAnalyticsManager
import ua.syt0r.kanji.core.review.LocalReviewManager
import ua.syt0r.kanji.core.review.ReviewManager
import ua.syt0r.kanji.core.review.SetupReview
import ua.syt0r.kanji.core.review.StartReview
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.DrawResult
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui.WritingPracticeScreenUI


@Composable
fun GooglePlayWritingPracticeScreen(
    configuration: WritingPracticeConfiguration,
    mainNavigationState: MainNavigationState,
    viewModel: WritingPracticeScreenContract.ViewModel = hiltViewModel<WritingPracticeViewModel>(),
) {

    LaunchedEffect(Unit) {
        viewModel.init(configuration)
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

    Review(state)

}

@Composable
private fun Review(
    state: State<ScreenState>,
    reviewManager: ReviewManager = LocalReviewManager.current
) {

    reviewManager.SetupReview()

    val shouldStartReview by remember {
        derivedStateOf {
            state.value.let { it is ScreenState.Summary.Saved && it.eligibleForInAppReview }
        }
    }

    if (shouldStartReview) {
        reviewManager.StartReview()
        LocalAnalyticsManager.current.sendEvent("starting_in_app_review")
    }

}