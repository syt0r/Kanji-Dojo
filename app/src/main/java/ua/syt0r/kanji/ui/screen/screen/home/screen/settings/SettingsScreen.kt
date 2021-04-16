package ua.syt0r.kanji.ui.screen.screen.home.screen.settings

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import ua.syt0r.kanji.di.getViewModel
import ua.syt0r.kanji.ui.screen.LocalMainNavigator
import ua.syt0r.kanji.ui.screen.MainContract

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenContract.ViewModel = getViewModel<SettingsViewModel>(),
    navigator: MainContract.Navigation = LocalMainNavigator.current
) {

    Button(
        onClick = { navigator.navigateToAbout() }
    ) {

        Text(text = "Go to About Screen")

    }

}