package ua.syt0r.kanji.ui.screen.screen.home.screen.settings

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.getViewModel
import ua.syt0r.kanji.ui.navigation.NavigationContract
import ua.syt0r.kanji.ui.navigation.getNavigator
import ua.syt0r.kanji.ui.screen.screen.about.AboutNavDestination

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenContract.ViewModel = getViewModel<SettingsViewModel>(),
    navigator: NavigationContract.Navigator = getNavigator()
) {

    Button(onClick = { navigator.navigate(AboutNavDestination.createRoute()) }) {
        Text(text = "Go to About Screen")
    }

}