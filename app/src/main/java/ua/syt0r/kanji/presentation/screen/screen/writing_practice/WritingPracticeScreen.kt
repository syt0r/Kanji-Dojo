package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import ua.syt0r.kanji.presentation.common.asActivity
import ua.syt0r.kanji.presentation.screen.MainActivity
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawResult
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui.WritingPracticeScreenUI

@Composable
fun WritingPracticeScreen(
    configuration: WritingPracticeConfiguration,
    navigation: MainContract.Navigation,
    viewModel: WritingPracticeScreenContract.ViewModel = hiltViewModel<WritingPracticeViewModel>(),
) {

    LaunchedEffect(Unit) {
        viewModel.init(configuration)
    }

    val state = viewModel.state

    WritingPracticeScreenUI(
        screenState = state.value,
        onUpClick = { navigation.navigateBack() },
        submitUserInput = viewModel::submitUserDrawnPath,
        onAnimationCompleted = {
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
        onPracticeCompleteButtonClick = { navigation.popUpToHome() }
    )

    val activity = LocalContext.current.asActivity() as MainActivity

    when (state.value) {
        is WritingPracticeScreenContract.ScreenState.Loading -> {
            LaunchedEffect(Unit) {
                activity.inAppReviewManager.prepareForReview()
            }
        }
        is WritingPracticeScreenContract.ScreenState.Summary.Saved -> {
            LaunchedEffect(Unit) {
                delay(2000)
                activity.inAppReviewManager.requestReview(activity)
            }
        }
        else -> {}
    }

}
