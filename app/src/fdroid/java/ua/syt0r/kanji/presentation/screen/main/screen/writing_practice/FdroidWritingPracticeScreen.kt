package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.DrawResult
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui.WritingPracticeScreenUI


@Composable
fun FdroidWritingPracticeScreen(
    configuration: PracticeScreenConfiguration.Writing,
    mainNavigationState: MainNavigationState,
    viewModel: WritingPracticeScreenContract.ViewModel = hiltViewModel<WritingPracticeViewModel>(),
) {

    LaunchedEffect(Unit) {
        viewModel.init(configuration)
        viewModel.reportScreenShown(configuration)
    }

    WritingPracticeScreenUI(
        state = viewModel.state,
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

}
