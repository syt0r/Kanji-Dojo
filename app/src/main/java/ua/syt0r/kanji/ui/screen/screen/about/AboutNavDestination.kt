package ua.syt0r.kanji.ui.screen.screen.about

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.ui.navigation.NoArgumentsScreenNavDestination

object AboutNavDestination : NoArgumentsScreenNavDestination() {

    override val routeName: String = "about"

    @Composable
    override fun ShowScreen() {
        AboutScreen()
    }


}