package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.feedback.FeedbackScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.feedback.FeedbackScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.feedback.FeedbackScreenUI

@Composable
private fun BasePreview(screenState: ScreenState) {
    AppTheme {
        FeedbackScreenUI(
            screenState = screenState,
            navigateBack = {},
            submitFeedback = {}
        )
    }
}

@Preview
@Composable
private fun IdlePreview() {
    BasePreview(
        screenState = ScreenState(
            MutableStateFlow(FeedbackScreenContract.FeedbackState.Editing),
            flow { }
        )
    )
}

@Preview
@Composable
private fun CompletePreview() {
    BasePreview(
        screenState = ScreenState(
            MutableStateFlow(FeedbackScreenContract.FeedbackState.Completed),
            flow { }
        )
    )
}