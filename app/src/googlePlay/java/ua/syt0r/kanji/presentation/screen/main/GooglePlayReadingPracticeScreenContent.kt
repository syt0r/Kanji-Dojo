package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import org.koin.androidx.compose.get
import ua.syt0r.kanji.core.review.AppReviewContract
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.DefaultReadingPracticeScreenContent
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract.ScreenState

object GooglePlayReadingPracticeScreenContent : ReadingPracticeContract.Content {

    @Composable
    override fun Draw(
        configuration: MainDestination.Practice.Reading,
        mainNavigationState: MainNavigationState,
        viewModel: ReadingPracticeContract.ViewModel
    ) {

        DefaultReadingPracticeScreenContent.Draw(
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