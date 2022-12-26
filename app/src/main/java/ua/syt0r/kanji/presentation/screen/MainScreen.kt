package ua.syt0r.kanji.presentation.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import ua.syt0r.kanji.core.analytics.LocalAnalytics

@Composable
fun MainScreen(
    viewModel: MainContract.ViewModel = hiltViewModel<MainViewModel>()
) {

    val navController = rememberNavController()

    val mainNavigation = MainNavigation(
        navHostController = navController,
        mainViewModel = viewModel,
        analyticsManager = LocalAnalytics.current
    )
    mainNavigation.DrawContent()

}