package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.*
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.presentation.screen.main.screen.about.AboutScreenUI
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.MultiplatformHomeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.rememberNewHomeNavigationState

interface MainNavigationState {
    fun navigateBack()
    fun popUpToHome()
    fun navigate(destination: MainDestination)
}

interface MultiplatformMainNavigationState : MainNavigationState {
    val currentDestination: State<MainDestination>
}

sealed interface MainDestination {

    object Home : MainDestination
    object About : MainDestination

    object ImportPractice : MainDestination
    sealed interface CreatePractice : MainDestination {

        object New : CreatePractice

        data class EditExisting(
            val practiceId: Long
        ) : CreatePractice

        data class Import(
            val title: String,
            val classification: CharactersClassification
        ) : CreatePractice

    }

    data class PracticePreview(
        val id: Long
    ) : MainDestination

    sealed interface Practice : MainDestination {

        data class Writing(
            val practiceId: Long,
            val characterList: List<String>,
            val isStudyMode: Boolean
        ) : Practice

        data class Reading(
            val practiceId: Long,
            val characterList: List<String>
        ) : Practice

    }

    data class KanjiInfo(
        val character: String
    ) : MainDestination

}

@Composable
fun MultiplatformMainNavigation(
    state: MainNavigationState
) {

    state as MultiplatformMainNavigationState

    val coroutineScope = rememberCoroutineScope()

    val homeNavigationState = rememberNewHomeNavigationState()
    val homeViewModel: HomeScreenContract.ViewModel = remember {
        KoinJavaComponent.getKoin().get { parametersOf(coroutineScope) }
    }

    val destination = state.currentDestination.value

    when (destination) {
        MainDestination.Home -> {
            MultiplatformHomeScreen(
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