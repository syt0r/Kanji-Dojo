package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.*
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
            state.value.let { it is ScreenState.Summary.Saved && it.eligibleForInAppReview }
        }
    }

    if (shouldStartReview) {
        val reviewManager = get<ReviewManager>()
        reviewManager.StartReview()
    }
}