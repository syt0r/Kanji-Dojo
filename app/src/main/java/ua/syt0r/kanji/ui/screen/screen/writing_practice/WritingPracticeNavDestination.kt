package ua.syt0r.kanji.ui.screen.screen.writing_practice

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate

object WritingPracticeNavDestination {

    private const val ROUTE_NAME = "writing_practice"
    private const val KANJI_ARGUMENT_KEY = "kanji"

    fun setup(navGraphBuilder: NavGraphBuilder) {

        navGraphBuilder.composable(
            route = "$ROUTE_NAME/{$KANJI_ARGUMENT_KEY}",
            arguments = listOf(
                navArgument(KANJI_ARGUMENT_KEY) { type = NavType.StringType }
            ),
            content = {

                val kanjiList = it.arguments!!
                    .getString(KANJI_ARGUMENT_KEY)!!
                    .toCharArray()
                    .map { it.toString() }

                ReviewScreenTest(

                    ReviewKanjiData(
                        kanji = "太",
                        onYomiReadings = listOf("た。いる"),
                        kunYomiReading = listOf("ゴン", "ゲン"),
                        meaningVariants = listOf("meaningless", "brbr")
                    )

                )

            }
        )

    }

    fun navigate(navHostController: NavHostController, kanjiList: List<String>) {
        navHostController.navigate("$ROUTE_NAME/${kanjiList.joinToString("")}")
    }

}