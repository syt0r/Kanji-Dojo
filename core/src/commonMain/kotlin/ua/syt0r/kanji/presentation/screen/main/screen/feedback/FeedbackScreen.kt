package ua.syt0r.kanji.presentation.screen.main.screen.feedback

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState

@Composable
fun FeedbackScreen(
    feedbackTopic: FeedbackTopic,
    mainNavigationState: MainNavigationState,
    viewModel: FeedbackScreenContract.ViewModel = getMultiplatformViewModel()
) {

    FeedbackScreenUI(
        feedbackTopic = feedbackTopic,
        screenState = viewModel.state,
        navigateBack = { mainNavigationState.navigateBack() },
        submitFeedback = { viewModel.sendFeedback(it) }
    )

}
