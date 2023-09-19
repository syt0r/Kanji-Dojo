package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.ui.PracticeDashboardScreenUI

@Composable
fun PracticeDashboardScreen(
    mainNavigationState: MainNavigationState,
    viewModel: PracticeDashboardScreenContract.ViewModel
) {

    LaunchedEffect(Unit) {
        viewModel.reportScreenShown()
    }

    PracticeDashboardScreenUI(
        state = viewModel.state,
        onImportPredefinedSet = {
            mainNavigationState.navigate(MainDestination.ImportPractice)
        },
        onCreateCustomSet = {
            mainNavigationState.navigate(MainDestination.CreatePractice.New)
        },
        onPracticeSetSelected = {
            mainNavigationState.navigate(MainDestination.PracticePreview(it.practiceId))
        },
        quickPractice = {
            mainNavigationState.navigate(it)
        },
        updateDailyGoalConfiguration = {
            viewModel.updateDailyGoal(it)
        }
    )

}
