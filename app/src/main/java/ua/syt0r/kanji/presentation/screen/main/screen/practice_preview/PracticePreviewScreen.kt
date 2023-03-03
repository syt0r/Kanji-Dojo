package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.ui.PracticePreviewScreenUI

@Composable
fun PracticePreviewScreen(
    practiceId: Long,
    mainNavigationState: MainNavigationState,
    viewModel: PracticePreviewScreenContract.ViewModel = hiltViewModel<PracticePreviewViewModel>(),
) {

    val shouldInvalidateData = rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (shouldInvalidateData.value) {
            viewModel.updateScreenData(practiceId)
            shouldInvalidateData.value = false
        }
        viewModel.reportScreenShown()
    }

    PracticePreviewScreenUI(
        state = viewModel.state,
        onConfigurationUpdated = { viewModel.updateConfiguration(it) },
        onUpButtonClick = { mainNavigationState.navigateBack() },
        onEditButtonClick = {
            shouldInvalidateData.value = true
            val configuration = CreatePracticeConfiguration.EditExisting(practiceId)
            mainNavigationState.navigateToPracticeCreate(configuration)
        },
        selectAllClick = { viewModel.selectAll() },
        deselectAllClick = { viewModel.deselectAll() },
        onCharacterClick = { mainNavigationState.navigateToKanjiInfo(it) },
        onStartPracticeClick = { group, practiceConfiguration ->
            shouldInvalidateData.value = true
            when (
                val configuration = viewModel.getPracticeConfiguration(group, practiceConfiguration)
            ) {
                is PracticeScreenConfiguration.Writing -> {
                    mainNavigationState.navigateToWritingPractice(configuration)
                }
                is PracticeScreenConfiguration.Reading -> {
                    mainNavigationState.navigateToReadingPractice(configuration)
                }
            }
        },
        onDismissMultiselectClick = { viewModel.toggleMultiSelectMode() },
        onEnableMultiselectClick = { viewModel.toggleMultiSelectMode() },
        onGroupClickInMultiselectMode = { viewModel.toggleSelectionForGroup(it) },
        onMultiselectPracticeStart = {
            shouldInvalidateData.value = true
            when (val configuration = viewModel.getPracticeConfiguration(it)) {
                is PracticeScreenConfiguration.Writing -> {
                    mainNavigationState.navigateToWritingPractice(configuration)
                }
                is PracticeScreenConfiguration.Reading -> {
                    mainNavigationState.navigateToReadingPractice(configuration)
                }
            }
        }
    )

}
