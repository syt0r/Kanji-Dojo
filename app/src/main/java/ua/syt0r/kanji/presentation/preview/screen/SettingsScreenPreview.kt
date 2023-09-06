package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenUI

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    AppTheme(
        useDarkTheme = false
    ) {
        SettingsScreenUI(
            state = ScreenState.Loaded(
                analyticsEnabled = false,
                noTranslationLayoutEnabled = false,
                leftHandedModeEnabled = false
            ).run { rememberUpdatedState(this) },
            onNoTranslationToggled = {},
            leftHandedToggled = {},
            onAnalyticsToggled = {},
            onAboutButtonClick = {}
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_C)
@Composable
private fun TabletPreview() {
    SettingsPreview()
}