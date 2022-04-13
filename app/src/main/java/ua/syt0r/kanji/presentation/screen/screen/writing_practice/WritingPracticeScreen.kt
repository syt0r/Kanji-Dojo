package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawResult
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui.WritingPracticeScreenUI

@Composable
fun WritingPracticeScreen(
    navigation: MainContract.Navigation,
    mainViewModel: MainContract.ViewModel,
    viewModel: WritingPracticeScreenContract.ViewModel = hiltViewModel<WritingPracticeViewModel>(),
) {

    viewModel.init(mainViewModel.currentPracticeConfiguration!!)
    val state = viewModel.state

    WritingPracticeScreenUI(
        screenState = state.value,
        onUpClick = { navigation.navigateBack() },
        submitUserInput = viewModel::submitUserDrawnPath,
        onAnimationCompleted = {
            when (it) {
                is DrawResult.Correct -> viewModel.handleCorrectlyDrawnStroke()
                is DrawResult.Mistake -> viewModel.handleIncorrectlyDrawnStroke()
            }
        },
        onPracticeCompleteButtonClick = { navigation.navigateToHome() }
    )

}
