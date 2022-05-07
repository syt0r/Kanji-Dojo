package ua.syt0r.kanji.presentation.screen.screen.practice_create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.practice_create.CreateWritingPracticeScreenContract.*
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_create.ui.CreateWritingPracticeScreenUI

@Composable
fun CreateWritingPracticeScreen(
    configuration: CreatePracticeConfiguration,
    mainNavigation: MainContract.Navigation,
    viewModel: ViewModel = hiltViewModel<CreateWritingPracticeViewModel>(),
) {

    LaunchedEffect(Unit) { viewModel.initialize(configuration) }

    val screenState = viewModel.state.value

    val isSaveCompleted = screenState is ScreenState.Loaded &&
            screenState.currentDataAction == DataAction.SaveCompleted
    if (isSaveCompleted) {
        LaunchedEffect(Unit) {
            delay(200)
            if (configuration is CreatePracticeConfiguration.EditExisting) {
                mainNavigation.navigateBack()
            } else {
                mainNavigation.popUpToHome()
            }
        }
    }

    val isDeleteCompleted = screenState is ScreenState.Loaded &&
            screenState.currentDataAction == DataAction.DeleteCompleted
    if (isDeleteCompleted) {
        LaunchedEffect(isDeleteCompleted) {
            delay(200)
            mainNavigation.popUpToHome()
        }
    }

    CreateWritingPracticeScreenUI(
        configuration = configuration,
        screenState = screenState,
        onUpClick = {
            mainNavigation.navigateBack()
        },
        onPracticeDeleteClick = {
            viewModel.deletePractice()
        },
        onCharacterInfoClick = {
            mainNavigation.navigateToKanjiInfo(it)
        },
        onCharacterDeleteClick = {
            viewModel.remove(it)
        },
        onCharacterRemovalCancel = {
            viewModel.cancelRemoval(it)
        },
        onSaveConfirmed = {
            viewModel.savePractice(it)
        },
        submitKanjiInput = {
            viewModel.submitUserInput(it)
        }
    )

}
