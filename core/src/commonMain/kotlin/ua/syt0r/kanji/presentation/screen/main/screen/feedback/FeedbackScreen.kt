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
        navigateBack = { mainNavigationState.navigateBack() }
    )

}
