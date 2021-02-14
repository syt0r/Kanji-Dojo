package ua.syt0r.kanji.ui.screen.screen.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import ua.syt0r.kanji.ui.navigation.NavDestination

object HomeNavDestination : NavDestination() {

    override val route = "home"

    override val contentProvider: @Composable (NavBackStackEntry) -> Unit = {
        HomeScreen()
    }

}