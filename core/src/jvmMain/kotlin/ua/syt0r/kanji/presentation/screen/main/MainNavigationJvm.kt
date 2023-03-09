package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable

@Composable
actual fun rememberMainNavigationState(): MainNavigationState =
    rememberMultiplatformMainNavigationState()

@Composable
actual fun MainNavigation(state: MainNavigationState) = MultiplatformMainNavigation(state)
