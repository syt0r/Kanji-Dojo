package ua.syt0r.kanji.presentation.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContract

object GooglePlaySettingsScreenContent : SettingsScreenContract.Content {

    @Composable
    override fun Draw(
        mainNavigationState: MainNavigationState
    ) {

        val viewModel = getMultiplatformViewModel<GooglePlaySettingsScreenContract.ViewModel>()

        LaunchedEffect(Unit) {
            viewModel.refresh()
            viewModel.reportScreenShown()
        }

        GooglePlaySettingsScreenUI(
            state = viewModel.state,
            onReminderConfigurationChange = { viewModel.updateReminder(it) },
            onAboutButtonClick = { mainNavigationState.navigate(MainDestination.About) },
            onBackupButtonClick = { mainNavigationState.navigate(MainDestination.Backup) },
            onAnalyticsToggled = { viewModel.updateAnalyticsEnabled(it) }
        )

    }

}