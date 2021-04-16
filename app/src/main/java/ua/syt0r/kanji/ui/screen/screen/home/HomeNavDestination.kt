package ua.syt0r.kanji.ui.screen.screen.home

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.ui.navigation.NoArgumentsScreenNavDestination

object HomeNavDestination : NoArgumentsScreenNavDestination() {

    override val routeName = "home"

    @Composable
    override fun ShowScreen() {
        HomeScreen()
    }

}