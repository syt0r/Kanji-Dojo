package ua.syt0r.kanji.ui.screen.screen.create_custom_set

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.ui.navigation.NoArgumentsScreenNavDestination

object CreateCustomSetNavDestination : NoArgumentsScreenNavDestination() {

    override val routeName = "create_custom_practice_set"

    @Composable
    override fun ShowScreen() {
        CreateCustomPracticeSetScreen()
    }

}