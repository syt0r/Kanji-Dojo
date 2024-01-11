package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import org.koin.androidx.compose.get
import ua.syt0r.kanji.core.review.AppReviewContract
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.DefaultWritingPracticeScreenContent
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState

object GooglePlayWritingPracticeScreenContent : WritingPracticeScreenContract.Content {

    @Composable
    override fun Draw(
        configuration: MainDestination.Practice.Writing,
        mainNavigationState: MainNavigationState,
        viewModel: WritingPracticeScreenContract.ViewModel
    ) {

        DefaultWritingPracticeScreenContent.Draw(
            configuration = configuration,
            mainNavigationState = mainNavigationState,
            viewModel = viewModel
        )

        ReviewHandler(state = viewModel.state)

    }

    @Composable
    private fun ReviewHandler(state: State<ScreenState>) {

        val canStartReview = remember {
            derivedStateOf { state.value is ScreenState.Saved }
        }

        if (canStartReview.value) {
            val reviewManager = get<AppReviewContract.ReviewManager>()
            reviewManager.AttemptReview()
        }

    }

}