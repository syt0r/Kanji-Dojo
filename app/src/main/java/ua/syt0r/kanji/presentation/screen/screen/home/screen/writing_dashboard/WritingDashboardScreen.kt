package ua.syt0r.kanji.presentation.screen.screen.home.screen.writing_dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.home.screen.writing_dashboard.ui.WritingDashboardScreenUI

@Composable
fun WritingDashboardScreen(
    mainNavigation: MainContract.Navigation,
    viewModel: WritingDashboardScreenContract.ViewModel = hiltViewModel<WritingDashboardViewModel>()
) {

    val mutableState = viewModel.state.observeAsState(
        initial = WritingDashboardScreenContract.State.Loading
    )

    viewModel.refreshData()

    WritingDashboardScreenUI(
        state = mutableState.value,
        onUpButtonClick = { mainNavigation.navigateBack() },
        onImportPredefinedSet = {
            viewModel.invalidateData()
            mainNavigation.navigateToWritingPracticeImport()
        },
        onCreateCustomSet = {
            viewModel.invalidateData()
            mainNavigation.navigateToWritingPracticeCreate()
        },
        onPracticeSetSelected = { mainNavigation.navigateToWritingPracticePreview(it.id) }
    )

}
