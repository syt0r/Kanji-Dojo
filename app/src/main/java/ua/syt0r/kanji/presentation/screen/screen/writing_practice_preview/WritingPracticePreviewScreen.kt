package ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.common.observeAsNonNullState
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview.WritingPracticePreviewScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview.ui.WritingPracticePreviewScreenUI

@Composable
fun WritingPracticePreviewScreen(
    practiceId: Long,
    practiceName: String,
    navigation: MainContract.Navigation,
    viewModel: WritingPracticePreviewScreenContract.ViewModel = hiltViewModel<WritingPracticePreviewViewModel>()
) {

    val mutableState = viewModel.state.observeAsNonNullState()

    if (mutableState.value == State.Init) {
        viewModel.loadPracticeInfo(practiceId)
    }

    WritingPracticePreviewScreenUI(
        practiceName = practiceName,
        state = mutableState.value,
        onUpButtonClick = { navigation.navigateBack() },
        onPracticeStart = { navigation.navigateToWritingPractice(it) },
        onKanjiClicked = { navigation.navigateToKanjiInfo(it) }
    )

}
