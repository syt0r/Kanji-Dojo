package ua.syt0r.kanji.presentation.screen.main.screen.feedback

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState

@Composable
fun FeedbackScreen(
    mainNavigationState: MainNavigationState,
    viewModel: FeedbackScreenContract.ViewModel = getMultiplatformViewModel()
) {

    FeedbackScreenUI(
        screenState = viewModel.state,
        navigateBack = { mainNavigationState.navigateBack() },
        submitFeedback = { viewModel.sendFeedback(it) }
    )

}
