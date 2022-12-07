package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.review.LocalReviewManager
import ua.syt0r.kanji.core.review.ReviewManager
import ua.syt0r.kanji.core.review.SetupReview
import ua.syt0r.kanji.core.review.StartReview
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawResult
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui.WritingPracticeScreenUI

@Composable
fun WritingPracticeScreen(
    configuration: WritingPracticeConfiguration,
    navigation: MainContract.Navigation,
    viewModel: WritingPracticeScreenContract.ViewModel = hiltViewModel<WritingPracticeViewModel>(),
    reviewManager: ReviewManager = LocalReviewManager.current
) {

    LaunchedEffect(Unit) {
        viewModel.init(configuration)
    }

    val state = viewModel.state

    WritingPracticeScreenUI(
        screenState = state.value,
        onUpClick = { navigation.navigateBack() },
        submitUserInput = {
            Logger.d("User draw[$it]")
            viewModel.submitUserDrawnPath(it)
        },
        onAnimationCompleted = {
            Logger.d("Animation completed[$it]")
            when (it) {
                is DrawResult.Correct -> viewModel.handleCorrectlyDrawnStroke()
                is DrawResult.Mistake -> viewModel.handleIncorrectlyDrawnStroke()
                DrawResult.IgnoreCompletedPractice -> {}
            }
        },
        onHintClick = {
            viewModel.handleIncorrectlyDrawnStroke()
        },
        onReviewItemClick = { navigation.navigateToKanjiInfo(it.characterReviewResult.character) },
        onPracticeCompleteButtonClick = { navigation.navigateBack() },
        onNextClick = { viewModel.loadNextCharacter() }
    )

    reviewManager.SetupReview()
    val shouldStartReview = state.value.let {
        it is WritingPracticeScreenContract.ScreenState.Summary.Saved && it.eligibleForInAppReview
    }
    if (shouldStartReview) {
        reviewManager.StartReview()
    }

}
