package ua.syt0r.kanji.presentation.screen.screen.home.screen.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.syt0r.kanji.presentation.screen.MainContract

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenContract.ViewModel = viewModel<SettingsViewModel>(),
    navigation: MainContract.Navigation
) {

    SettingsScreenUI(
        onAboutButtonClick = { navigation.navigateToAbout() }
    )

}
