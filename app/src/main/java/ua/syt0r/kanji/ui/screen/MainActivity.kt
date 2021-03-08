package ua.syt0r.kanji.ui.screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ua.syt0r.kanji.ui.navigation.LocalNavigator
import ua.syt0r.kanji.ui.navigation.Navigator
import ua.syt0r.kanji.ui.screen.screen.about.AboutNavDestination
import ua.syt0r.kanji.ui.screen.screen.home.HomeNavDestination
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            KanjiDojoTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }

        }

    }

}

@Composable
private fun MainScreen() {

    val navController = rememberNavController()
    val navigator = Navigator(navController)

    CompositionLocalProvider(LocalNavigator provides navigator) {
        MainScreenNavigationRoutes(navController)
    }

}

@Composable
private fun MainScreenNavigationRoutes(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = HomeNavDestination.route
    ) {

        HomeNavDestination.setupRoute(this)
        AboutNavDestination.setupRoute(this)

    }

}