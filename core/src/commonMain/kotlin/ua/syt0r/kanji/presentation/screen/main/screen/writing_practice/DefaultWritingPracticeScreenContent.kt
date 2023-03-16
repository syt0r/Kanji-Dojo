package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui.WritingPracticeScreenUI

object DefaultWritingPracticeScreenContent : WritingPracticeScreenContract.Content {

    @Composable
    override fun Draw(
        configuration: MainDestination.Practice.Writing,
        mainNavigationState: MainNavigationState,
        viewModel: WritingPracticeScreenContract.ViewModel
    ) {

        LaunchedEffect(Unit) {
            viewModel.init(configuration)
            viewModel.reportScreenShown(configuration)
        }

        WritingPracticeScreenUI(
            state = viewModel.state,
            navigateBack = { mainNavigationState.navigateBack() },
            submitUserInput = { viewModel.submitUserDrawnPath(it) },
            onHintClick = { viewModel.onHintClick() },
            onReviewItemClick = {
                mainNavigationState.navigate(
                    MainDestination.KanjiInfo(it.characterReviewResult.character)
                )
            },
            onPracticeCompleteButtonClick = { mainNavigationState.navigateBack() },
            onNextClick = { viewModel.loadNextCharacter(it) },
            toggleRadicalsHighlight = { viewModel.toggleRadicalsHighlight() }
        )

    }

}