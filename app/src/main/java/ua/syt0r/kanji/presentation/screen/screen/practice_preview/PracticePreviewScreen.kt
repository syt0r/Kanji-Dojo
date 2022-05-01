package ua.syt0r.kanji.presentation.screen.screen.practice_preview

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.data.SelectionOption
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.ui.PracticePreviewScreenUI
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.WritingPracticeConfiguration

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WritingPracticePreviewScreen(
    practiceId: Long,
    practiceTitle: String,
    navigation: MainContract.Navigation,
    viewModel: PracticePreviewScreenContract.ViewModel = hiltViewModel<PracticePreviewViewModel>(),
) {

    val screenState = viewModel.state.value

    LaunchedEffect(Unit) {
        viewModel.loadPracticeInfo(practiceId)
    }

    PracticePreviewScreenUI(
        title = practiceTitle,
        screenState = screenState,
        onUpButtonClick = { navigation.navigateBack() },
        onCloseButtonClick = {},
        onEditButtonClick = {
            val configuration = CreatePracticeConfiguration.EditExisting(
                practiceId = practiceId
            )
            navigation.navigateToPracticeCreate(configuration)
        },
        onSortSelected = {},
        onPracticeModeSelected = {},
        onShuffleSelected = {},
        onOptionSelected = {},
        onInputChanged = {},
        onFloatingButtonClick = {
            screenState as ScreenState.Loaded
            navigation.navigateToWritingPractice(
                WritingPracticeConfiguration(practiceId, screenState.selectedCharacters)
            )
        },
        onCharacterClick = {
            screenState as ScreenState.Loaded
            if (screenState.selectionConfig.option == SelectionOption.ManualSelection) {

            } else {
                navigation.navigateToKanjiInfo(it)
            }
        }
    )

}
