package ua.syt0r.kanji.ui.screen.screen.kanji_info

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument

object KanjiInfoNavDestination {

    private const val ROUTE_NAME = "kanji_info"
    private const val KANJI_ARGUMENT_KEY = "kanji"

    fun setup(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(
            route = "$ROUTE_NAME/{$KANJI_ARGUMENT_KEY}",
            arguments = listOf(
                navArgument(KANJI_ARGUMENT_KEY) { type = NavType.StringType }
            ),
            content = {
                val kanji = it.arguments!!.getString(KANJI_ARGUMENT_KEY)!!
                KanjiInfoScreen(kanji = kanji)
            }
        )
    }

    fun navigate(navHostController: NavHostController, kanji: String) {
        navHostController.navigate("$ROUTE_NAME/$kanji")
    }

}