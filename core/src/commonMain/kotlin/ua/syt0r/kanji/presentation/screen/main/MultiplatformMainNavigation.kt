package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.screen.about.AboutScreen
import ua.syt0r.kanji.presentation.screen.main.screen.backup.BackupScreen
import ua.syt0r.kanji.presentation.screen.main.screen.feedback.FeedbackScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.home.rememberHomeNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.PracticeImportScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.PracticePreviewScreen
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreen


interface MultiplatformMainNavigationState : MainNavigationState {
    val currentDestination: State<MainDestination>
    val stateHolder: SaveableStateHolder
}

@Composable
fun MultiplatformMainNavigation(
    state: MainNavigationState
) {
    state as MultiplatformMainNavigationState

    val destination = state.currentDestination.value

    state.stateHolder.SaveableStateProvider(destination.toString()) {
        when (destination) {
            MainDestination.Home -> {
                val homeNavigationState = rememberHomeNavigationState()
                HomeScreen(
                    viewModel = getMultiplatformViewModel(),
                    mainNavigationState = rememberUpdatedState(state),
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

            is MainDestination.CreatePractice -> {
                PracticeCreateScreen(
                    configuration = destination,
                    mainNavigationState = state,
                    viewModel = getMultiplatformViewModel()
                )
            }

            is MainDestination.PracticePreview -> {
                PracticePreviewScreen(
                    practiceId = destination.id,
                    mainNavigationState = state,
                    viewModel = getMultiplatformViewModel()
                )
            }

            is MainDestination.Practice.Reading -> {
                ReadingPracticeScreen(
                    configuration = destination,
                    navigationState = state,
                    viewModel = getMultiplatformViewModel()
                )
            }

            is MainDestination.Practice.Writing -> {
                WritingPracticeScreen(
                    configuration = destination,
                    mainNavigationState = state,
                    viewModel = getMultiplatformViewModel()
                )
            }

            is MainDestination.KanjiInfo -> {
                key(destination) {
                    KanjiInfoScreen(
                        kanji = destination.character,
                        mainNavigationState = state,
                        viewModel = getMultiplatformViewModel()
                    )
                }
            }

            is MainDestination.Backup -> {
                BackupScreen(mainNavigationState = state)
            }

            is MainDestination.Feedback -> {
                FeedbackScreen(
                    feedbackTopic = destination.topic,
                    mainNavigationState = state
                )
            }
        }
    }

}

@Composable
fun rememberMultiplatformMainNavigationState(): MainNavigationState {
    val stack = remember { mutableStateOf<List<MainDestination>>(listOf(MainDestination.Home)) }
    val currentDestinationState = remember { derivedStateOf { stack.value.last() } }

    val stateHolder = rememberSaveableStateHolder()

    return remember {
        object : MultiplatformMainNavigationState {

            override val currentDestination = currentDestinationState
            override val stateHolder: SaveableStateHolder = stateHolder

            override fun navigateBack() {
                val lastItem = stack.value.last()
                stack.value = stack.value.dropLast(1)
                stateHolder.removeState(lastItem.toString())
            }

            override fun popUpToHome() {
                val itemsToRemove = stack.value.drop(1)
                stack.value = stack.value.take(1)
                itemsToRemove.forEach { stateHolder.removeState(it) }
            }

            override fun navigate(destination: MainDestination) {
                stack.value = stack.value.plus(destination)
            }

        }
    }
}