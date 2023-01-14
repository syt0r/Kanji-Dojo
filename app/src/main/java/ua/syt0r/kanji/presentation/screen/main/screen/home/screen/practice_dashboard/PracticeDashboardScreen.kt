package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.ui.PracticeDashboardScreenUI
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration

@Composable
fun PracticeDashboardScreen(
    mainNavigationState: MainNavigationState,
    viewModel: PracticeDashboardScreenContract.ViewModel = hiltViewModel<PracticeDashboardViewModel>()
) {

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    PracticeDashboardScreenUI(
        state = viewModel.state,
        onImportPredefinedSet = { mainNavigationState.navigateToPracticeImport() },
        onCreateCustomSet = {
            mainNavigationState.navigateToPracticeCreate(CreatePracticeConfiguration.NewPractice)
        },
        onPracticeSetSelected = { mainNavigationState.navigateToPracticePreview(it.id, it.name) },
        onAnalyticsSuggestionAccepted = {
            viewModel.enableAnalytics()
            viewModel.dismissAnalyticsSuggestion()
        },
        onAnalyticsSuggestionDismissed = { viewModel.dismissAnalyticsSuggestion() }
    )

}
