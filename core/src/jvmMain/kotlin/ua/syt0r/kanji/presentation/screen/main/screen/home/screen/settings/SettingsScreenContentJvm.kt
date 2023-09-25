package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState

object SettingsScreenContentJvm : SettingsScreenContract.Content {

    @Composable
    override fun Draw(mainNavigationState: MainNavigationState) {
        SettingsScreenUIJvm(
            onAboutButtonClick = { mainNavigationState.navigate(MainDestination.About) }
        )
    }

}