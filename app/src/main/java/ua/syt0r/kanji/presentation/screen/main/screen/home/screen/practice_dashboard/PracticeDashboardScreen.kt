package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.main.MainContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.ui.PracticeDashboardScreenUI
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration

@Composable
fun PracticeDashboardScreen(
    navigation: MainContract.Navigation,
    viewModel: PracticeDashboardScreenContract.ViewModel = hiltViewModel<PracticeDashboardViewModel>()
) {

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    PracticeDashboardScreenUI(
        state = viewModel.state,
        onImportPredefinedSet = { navigation.navigateToPracticeImport() },
        onCreateCustomSet = {
            navigation.navigateToPracticeCreate(CreatePracticeConfiguration.NewPractice)
        },
        onPracticeSetSelected = { navigation.navigateToPracticePreview(it.id, it.name) },
        onAnalyticsSuggestionDismissed = { viewModel.dismissAnalyticsSuggestion() }
    )

}
