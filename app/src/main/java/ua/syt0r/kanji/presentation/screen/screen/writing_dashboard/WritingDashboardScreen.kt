package ua.syt0r.kanji.presentation.screen.screen.writing_dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.writing_dashboard.ui.WritingDashboardScreenUI

@Composable
fun WritingDashboardScreen(
    viewModel: WritingDashboardScreenContract.ViewModel = hiltViewModel<WritingDashboardViewModel>(),
    mainNavigation: MainContract.Navigation
) {

    val mutableState = viewModel.state.observeAsState(
        initial = WritingDashboardScreenContract.State.Loading
    )

    WritingDashboardScreenUI(
        state = mutableState.value,
        onUpButtonClick = { mainNavigation.navigateBack() },
        onImportPredefinedSet = { mainNavigation.navigateToWritingPracticeImport() },
        onCreateCustomSet = { mainNavigation.navigateToWritingPracticeCreate() },
        onPracticeSetSelected = { mainNavigation.navigateToWritingPracticePreview(it.id) }
    )

}
