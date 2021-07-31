package ua.syt0r.kanji.presentation.screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    MainNavigation(navController).apply { DrawContent() }
}