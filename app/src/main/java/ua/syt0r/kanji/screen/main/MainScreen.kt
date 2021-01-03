package ua.syt0r.kanji.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.*
import ua.syt0r.kanji.screen.main.sub_screen.home.HomeScreen
import ua.syt0r.kanji.screen.main.sub_screen.kanji_test.KanjiTestScreen

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
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen {
                navController.navigate("kanji_test/$it") {
                }
            }
        }

        composable(
            route = "kanji_test/{kanji}",
            arguments = listOf(
                navArgument("kanji") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val kanji = backStackEntry.arguments!!.getInt("kanji")
            KanjiTestScreen(kanjiIndex = kanji)
        }


    }

}