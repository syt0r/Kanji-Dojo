package ua.syt0r.kanji.presentation.screen.screen.writing_practice_create

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.common.observeAsNonNullState
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_create.CreateWritingPracticeScreenContract.ViewModel
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_create.ui.CreateWritingPracticeScreenUI

@Composable
fun CreateWritingPracticeScreen(
    viewModel: ViewModel = hiltViewModel<CreateWritingPracticeViewModel>(),
    mainNavigation: MainContract.Navigation
) {

    val mutableState = viewModel.state.observeAsNonNullState()

    if (mutableState.value.stateType == CreateWritingPracticeScreenContract.StateType.Done) {
        mainNavigation.navigateToWritingDashboard()
    }

    CreateWritingPracticeScreenUI(
        state = mutableState.value,
        navigateBack = {
            mainNavigation.navigateBack()
        },
        onKanjiInputSubmitted = {
            viewModel.submitUserInput(it)
        },
        onCreateButtonClick = {
            viewModel.createSet(it)
        }
    )

}
