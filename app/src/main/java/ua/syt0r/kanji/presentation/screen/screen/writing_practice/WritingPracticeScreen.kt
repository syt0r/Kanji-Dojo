package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.common.observeAsNonNullState
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui.WritingPracticeScreenUI

@Composable
fun WritingPracticeScreen(
    practiceId: Long,
    navigation: MainContract.Navigation,
    viewModel: WritingPracticeScreenContract.ViewModel = hiltViewModel<WritingPracticeViewModel>(),
) {

    val mutableState = viewModel.state.observeAsNonNullState()

    if (mutableState.value == WritingPracticeScreenContract.State.Init) {
        viewModel.init(practiceId)
    }

    WritingPracticeScreenUI(
        state = mutableState.value,
        onUpClick = { navigation.navigateBack() }
    )

}
