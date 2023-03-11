package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenContract.ViewModel,
    mainNavigationState: MainNavigationState
) {

    LaunchedEffect(Unit) {
        viewModel.refresh()
        viewModel.reportScreenShown()
    }

    SettingsScreenUI(
        state = viewModel.state,
        onNoTranslationToggled = { viewModel.updateNoTranslationLayout(it) },
        onAnalyticsToggled = { viewModel.updateAnalyticsEnabled(it) },
        onAboutButtonClick = { mainNavigationState.navigate(MainDestination.About) }
    )

}
