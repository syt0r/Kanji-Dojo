package ua.syt0r.kanji.presentation.screen.screen.practice_import

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.common.observeAsNonNullState
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.practice_import.ui.WritingPracticeImportScreenUI

@Composable
fun WritingPracticeImportScreen(
    mainNavigation: MainContract.Navigation,
    viewModel: PracticeImportScreenContract.ViewModel = hiltViewModel<PracticeImportViewModel>()
) {

    val state = viewModel.state.observeAsNonNullState()
    WritingPracticeImportScreenUI(
        state = state.value,
        onUpButtonClick = { mainNavigation.navigateBack() }
    )

}
