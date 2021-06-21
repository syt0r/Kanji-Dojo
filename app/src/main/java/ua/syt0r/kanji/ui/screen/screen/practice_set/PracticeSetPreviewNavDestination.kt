package ua.syt0r.kanji.ui.screen.screen.practice_set

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.ui.navigation.NoArgumentsScreenNavDestination

object PracticeSetPreviewNavDestination : NoArgumentsScreenNavDestination() {

    override val routeName: String = "practice_set_preview"

    @Composable
    override fun ShowScreen() {
        PracticeSetPreviewScreen()
    }

}