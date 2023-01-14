package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenContract.ViewModel = hiltViewModel<SettingsViewModel>(),
    mainNavigationState: MainNavigationState
) {

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    SettingsScreenUI(
        state = viewModel.state,
        onAnalyticsToggled = { viewModel.updateAnalyticsEnabled(it) },
        onAboutButtonClick = { mainNavigationState.navigateToAbout() }
    )

}
