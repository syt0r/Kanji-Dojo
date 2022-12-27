package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import ua.syt0r.kanji.core.analytics.LocalAnalyticsManager

@Composable
fun MainScreen(
    viewModel: MainContract.ViewModel = hiltViewModel<MainViewModel>()
) {

    val navController = rememberNavController()

    val mainNavigation = MainNavigation(
        navHostController = navController,
        mainViewModel = viewModel,
        analyticsManager = LocalAnalyticsManager.current
    )
    mainNavigation.DrawContent()

}