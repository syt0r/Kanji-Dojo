package ua.syt0r.kanji.presentation.screen.screen.practice_create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.practice_create.CreateWritingPracticeScreenContract.ViewModel
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

    CreateWritingPracticeScreenUI(
        configuration = configuration,
        screenState = screenState,
        onUpClick = {
            mainNavigation.navigateBack()
        },
        onPracticeDeleteClick = {
            viewModel.deletePractice()
        },
        onDeleteAnimationCompleted = {
            mainNavigation.popUpToHome()
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
        onSaveAnimationCompleted = {
            if (configuration is CreatePracticeConfiguration.EditExisting) {
                mainNavigation.navigateBack()
            } else {
                mainNavigation.popUpToHome()
            }
        },
        submitKanjiInput = {
            viewModel.submitUserInput(it)
        }
    )

}
