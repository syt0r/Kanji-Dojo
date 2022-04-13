package ua.syt0r.kanji.presentation.screen.screen.practice_preview

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.common.observeAsNonNullState
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.ui.PracticePreviewScreenUI

@Composable
fun WritingPracticePreviewScreen(
    practiceId: Long,
    navigation: MainContract.Navigation,
    mainViewModel: MainContract.ViewModel,
    viewModel: PracticePreviewScreenContract.ViewModel = hiltViewModel<PracticePreviewViewModel>(),
) {

    val mutableState = viewModel.state.observeAsNonNullState()

    if (mutableState.value == State.Init) {
        viewModel.loadPracticeInfo(practiceId)
    }

    PracticePreviewScreenUI(
        state = mutableState.value,
        onUpButtonClick = { navigation.navigateBack() },
        onPracticeStart = {
            mainViewModel.currentPracticeConfiguration = it
            navigation.navigateToWritingPractice(it)
        },
        onKanjiClicked = { navigation.navigateToKanjiInfo(it) }
    )

}
