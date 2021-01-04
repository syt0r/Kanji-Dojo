package ua.syt0r.kanji.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.*
import ua.syt0r.kanji.screen.Navigation
import ua.syt0r.kanji.screen.main.sub_screen.home.HomeScreen
import ua.syt0r.kanji.screen.main.sub_screen.review.ReviewScreen

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navigation.Home.routeName
    ) {

        composable("home") {
            HomeScreen {
                navController.navigate(Navigation.KanjiTest.createRoute(kanji = it))
            }
        }

        composable(
            route = "kanji_test/{kanji}",
            arguments = listOf(
                navArgument(Navigation.KanjiTest.KANJI_INDEX_ARGUMENT_KEY) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val kanji = backStackEntry.arguments!!
                .getString(Navigation.KanjiTest.KANJI_INDEX_ARGUMENT_KEY)!!
            ReviewScreen(kanji = kanji)
        }


    }

}