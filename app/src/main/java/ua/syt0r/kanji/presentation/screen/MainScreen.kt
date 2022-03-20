package ua.syt0r.kanji.presentation.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(
    viewModel: MainContract.ViewModel = hiltViewModel<MainViewModel>()
) {
    val navController = rememberNavController()
    MainNavigation(navController, viewModel).apply { DrawContent() }
}