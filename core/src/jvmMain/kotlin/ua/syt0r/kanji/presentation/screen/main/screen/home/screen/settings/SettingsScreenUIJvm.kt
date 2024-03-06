package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import androidx.compose.runtime.Composable

@Composable
fun SettingsScreenUIJvm(
    onBackupButtonClick: () -> Unit,
    onAboutButtonClick: () -> Unit
) {

    SettingsContent {

        SettingsThemeToggle()

        SettingsBackupButton(onBackupButtonClick)

        SettingsAboutButton(onAboutButtonClick)

    }

}