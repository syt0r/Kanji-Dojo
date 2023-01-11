package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.main.MainContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.ui.PracticePreviewScreenUI

@Composable
fun PracticePreviewScreen(
    practiceId: Long,
    practiceTitle: String,
    navigation: MainContract.Navigation,
    viewModel: PracticePreviewScreenContract.ViewModel = hiltViewModel<PracticePreviewViewModel>(),
) {

    LaunchedEffect(Unit) {
        viewModel.loadPracticeInfo(practiceId)
    }

    PracticePreviewScreenUI(
        state = viewModel.state,
        onSortSelected = { viewModel.applySortConfig(it) },
        onUpButtonClick = { navigation.navigateBack() },
        onEditButtonClick = {
            val configuration = CreatePracticeConfiguration.EditExisting(practiceId)
            navigation.navigateToPracticeCreate(configuration)
        },
        onCharacterClick = { navigation.navigateToKanjiInfo(it) },
        onStartPracticeClick = { group, configuration ->
            val writingConfiguration = viewModel.getPracticeConfiguration(group, configuration)
            navigation.navigateToWritingPractice(writingConfiguration)
        },
        onDismissMultiselectClick = { viewModel.toggleMultiSelectMode() },
        onEnableMultiselectClick = { viewModel.toggleMultiSelectMode() },
        onGroupClickInMultiselectMode = { viewModel.toggleSelectionForGroup(it) },
        onMultiselectPracticeStart = {
            val configuration = viewModel.getPracticeConfiguration(it)
            navigation.navigateToWritingPractice(configuration)
        }
    )

}
