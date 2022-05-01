package ua.syt0r.kanji.presentation.screen.screen.home.screen.practice_dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.home.screen.practice_dashboard.ui.PracticeDashboardScreenUI
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration

@Composable
fun PracticeDashboardScreen(
    mainNavigation: MainContract.Navigation,
    viewModel: PracticeDashboardScreenContract.ViewModel = hiltViewModel<PracticeDashboardViewModel>()
) {

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    val screenState = viewModel.state.value

    PracticeDashboardScreenUI(
        screenState = screenState,
        onImportPredefinedSet = {
            mainNavigation.navigateToPracticeImport()
        },
        onCreateCustomSet = {
            mainNavigation.navigateToPracticeCreate(CreatePracticeConfiguration.NewPractice)
        },
        onPracticeSetSelected = {
            mainNavigation.navigateToPracticePreview(it.id, it.name)
        }
    )

}
