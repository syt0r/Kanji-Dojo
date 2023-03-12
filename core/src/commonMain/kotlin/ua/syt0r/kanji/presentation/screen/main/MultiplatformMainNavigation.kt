package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.*
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.screen.about.AboutScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.rememberHomeNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.PracticeImportScreen


interface MultiplatformMainNavigationState : MainNavigationState {
    val currentDestination: State<MainDestination>
}

@Composable
fun MultiplatformMainNavigation(
    state: MainNavigationState
) {

    state as MultiplatformMainNavigationState

    val homeNavigationState = rememberHomeNavigationState()
    val homeViewModel: HomeScreenContract.ViewModel = getMultiplatformViewModel()

    val destination = state.currentDestination.value

    when (destination) {
        MainDestination.Home -> {
            HomeScreen(
                viewModel = homeViewModel,
                mainNavigationState = state,
                homeNavigationState = homeNavigationState
            )
        }
        MainDestination.About -> {
            AboutScreen(
                mainNavigationState = state,
                viewModel = getMultiplatformViewModel()
            )
        }
        MainDestination.ImportPractice -> {
            PracticeImportScreen(
                mainNavigationState = state,
                viewModel = getMultiplatformViewModel()
            )
        }
        is MainDestination.CreatePractice -> TODO()
        is MainDestination.PracticePreview -> TODO()
        is MainDestination.Practice.Reading -> TODO()
        is MainDestination.Practice.Writing -> TODO()
        is MainDestination.KanjiInfo -> TODO()
    }

}

@Composable
fun rememberMultiplatformMainNavigationState(): MainNavigationState {
    val stack = remember { mutableStateOf<List<MainDestination>>(listOf(MainDestination.Home)) }
    val currentDestinationState = remember { derivedStateOf { stack.value.last() } }

    return remember {
        object : MultiplatformMainNavigationState {

            override val currentDestination = currentDestinationState

            override fun navigateBack() {
                stack.value = stack.value.dropLast(1)
            }

            override fun popUpToHome() {
                stack.value = stack.value.take(1)
            }

            override fun navigate(destination: MainDestination) {
                stack.value = stack.value.plus(destination)
            }

        }
    }
}