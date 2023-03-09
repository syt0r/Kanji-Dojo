package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.*
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent
import ua.syt0r.kanji.presentation.screen.main.screen.about.AboutScreenUI
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.rememberHomeNavigationState


interface MultiplatformMainNavigationState : MainNavigationState {
    val currentDestination: State<MainDestination>
}

@Composable
fun MultiplatformMainNavigation(
    state: MainNavigationState
) {

    state as MultiplatformMainNavigationState

    val coroutineScope = rememberCoroutineScope()

    val homeNavigationState = rememberHomeNavigationState()
    val homeViewModel: HomeScreenContract.ViewModel = remember {
        KoinJavaComponent.getKoin().get { parametersOf(coroutineScope) }
    }

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
            AboutScreenUI(
                onUpButtonClick = { state.navigateBack() }
            )
        }
        MainDestination.ImportPractice -> TODO()
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