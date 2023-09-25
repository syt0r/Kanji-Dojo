package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.koin.androidx.compose.get
import ua.syt0r.kanji.core.review.ReviewManager
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState


@Composable
fun GooglePlayWritingPracticeScreen(
    configuration: MainDestination.Practice.Writing,
    mainNavigationState: MainNavigationState,
    viewModel: WritingPracticeScreenContract.ViewModel,
) {

    DefaultWritingPracticeScreenContent.Draw(
        configuration = configuration,
        mainNavigationState = mainNavigationState,
        viewModel = viewModel
    )

    InAppReview(state = viewModel.state)

}

@Composable
private fun InAppReview(
    state: State<ScreenState>,
) {
    val shouldStartReview by remember {
        derivedStateOf {
            state.value.let { it is ScreenState.Saved }
        }
    }

    if (shouldStartReview) {
        val reviewManager = get<ReviewManager>()
        reviewManager.AttemptReview()
    }
}