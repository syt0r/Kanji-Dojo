package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(
    viewModel: MainContract.ViewModel = hiltViewModel<MainViewModel>()
) {

    val navigationState = rememberMainNavigationState(viewModel)
    MainNavigationContent(navigationState)

}