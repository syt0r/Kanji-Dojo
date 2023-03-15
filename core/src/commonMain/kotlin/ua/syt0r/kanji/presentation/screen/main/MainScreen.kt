package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable

@Composable
fun MainScreen() {
    val navigationState = rememberMainNavigationState()
    MainNavigation(navigationState)
}