package ua.syt0r.kanji.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(
    viewModel: MainContract.ViewModel = hiltViewModel<MainViewModel>()
) {

    val navController = rememberNavController()

    val mainNavigation = MainNavigation(
        navHostController = navController,
        mainViewModel = viewModel,
        analyticsManager = (LocalContext.current as MainActivity).analyticsManager
    )
    mainNavigation.DrawContent()

}