package ua.syt0r.kanji.presentation.screen.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreenUI

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    AppTheme {
        HomeScreenUI(
            availableTabs = HomeScreenTab.values().toList(),
            selectedTabState = HomeScreenTab.values().first().run { rememberUpdatedState(this) },
            onTabSelected = {},
            screenTabContent = { "PracticeDashboardUIPreview()" }
        )
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_C)
@Composable
private fun TabletPreview() {
    ua.syt0r.kanji.presentation.screen.preview.Preview()
}