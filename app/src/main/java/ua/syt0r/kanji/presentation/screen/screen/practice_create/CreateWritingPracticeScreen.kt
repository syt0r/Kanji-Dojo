package ua.syt0r.kanji.presentation.screen.screen.practice_create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
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

    if (screenState is ScreenState.Loaded && screenState.currentDataAction == DataAction.SaveCompleted) {
        mainNavigation.navigateBack()
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
        submitKanjiInput = {
            viewModel.submitUserInput(it)
        },
        onCharacterInfoClick = {
            mainNavigation.navigateToKanjiInfo(it)
        },
        onCharacterDeleteClick = {
            viewModel.removeCharacter(it)
        },
        onChangesConfirmationClick = {
            viewModel.savePractice(it)
        }
    )

}
