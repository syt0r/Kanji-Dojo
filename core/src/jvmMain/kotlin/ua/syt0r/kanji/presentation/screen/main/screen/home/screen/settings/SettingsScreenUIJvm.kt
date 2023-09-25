package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.runtime.Composable

@Composable
fun SettingsScreenUIJvm(
    onAboutButtonClick: () -> Unit
) {

    SettingsContent {

        SettingsThemeToggle()

        SettingsAboutButton(onAboutButtonClick)

    }

}