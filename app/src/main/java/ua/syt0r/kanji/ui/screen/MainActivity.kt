package ua.syt0r.kanji.ui.screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme

@AndroidEntryPoint
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
    val mainNavigation = MainNavigation(navController)

    CompositionLocalProvider(LocalMainNavigator provides mainNavigation) {
        mainNavigation.setup()
    }

}