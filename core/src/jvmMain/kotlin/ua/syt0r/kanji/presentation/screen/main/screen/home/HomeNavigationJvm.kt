package ua.syt0r.kanji.presentation.screen.main.screen.home

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState

@Composable
actual fun rememberHomeNavigationState(defaultTab: HomeScreenTab): HomeNavigationState {
    return rememberMultiplatformHomeNavigationState(defaultTab)
}

@Composable
actual fun HomeNavigationContent(
    homeNavigationState: HomeNavigationState,
    mainNavigationState: MainNavigationState
) {
    MultiplatformHomeNavigationContent(homeNavigationState, mainNavigationState)
}