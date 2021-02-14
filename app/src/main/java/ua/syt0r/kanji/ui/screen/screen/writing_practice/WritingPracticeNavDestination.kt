package ua.syt0r.kanji.ui.screen.screen.writing_practice

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import ua.syt0r.kanji.ui.navigation.NavDestination

object WritingPracticeNavDestination : NavDestination() {

    private const val BASE_ROUTE = "kanji_test"
    private const val KANJI_INDEX_ARGUMENT_KEY = "kanji"

    override val route = "$BASE_ROUTE/{kanji}"

    override val arguments = listOf(
        navArgument(KANJI_INDEX_ARGUMENT_KEY) { type = NavType.StringType }
    )

    override val contentProvider: @Composable (NavBackStackEntry) -> Unit = {
        val kanji = it.arguments!!.getString(KANJI_INDEX_ARGUMENT_KEY)!!
        ReviewScreenTest(
            ReviewKanjiData(
                kanji = "太",
                onYomiReadings = listOf("た。いる"),
                kunYomiReading = listOf("ゴン", "ゲン"),
                meaningVariants = listOf("meaningless", "brbr")
            )
        )
    }

}