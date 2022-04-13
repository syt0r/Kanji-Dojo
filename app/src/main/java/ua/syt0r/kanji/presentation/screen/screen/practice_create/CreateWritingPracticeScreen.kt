package ua.syt0r.kanji.presentation.screen.screen.practice_create

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.common.observeAsNonNullState
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.practice_create.CreateWritingPracticeScreenContract.ViewModel
import ua.syt0r.kanji.presentation.screen.screen.practice_create.ui.CreateWritingPracticeScreenUI

@Composable
fun CreateWritingPracticeScreen(
    viewModel: ViewModel = hiltViewModel<CreateWritingPracticeViewModel>(),
    mainNavigation: MainContract.Navigation,
    initialKanjiList: List<String> = emptyList()
) {

    viewModel.initialize(initialKanjiList)

    val mutableState = viewModel.state.observeAsNonNullState()

    if (mutableState.value.stateType == CreateWritingPracticeScreenContract.StateType.Done) {
        mainNavigation.navigateBack()
    }

    CreateWritingPracticeScreenUI(
        state = mutableState.value,
        onUpClick = {
            mainNavigation.navigateBack()
        },
        submitKanjiInput = {
            viewModel.submitUserInput(it)
        },
        createPractice = {
            viewModel.createSet(it)
        }
    )

}
