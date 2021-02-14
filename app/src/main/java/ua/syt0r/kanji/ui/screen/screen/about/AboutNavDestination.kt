package ua.syt0r.kanji.ui.screen.screen.about

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import ua.syt0r.kanji.ui.navigation.NavDestination

object AboutNavDestination : NavDestination() {

    override val route = "about"

    override val contentProvider: @Composable (NavBackStackEntry) -> Unit = {
        AboutScreen()
    }

}