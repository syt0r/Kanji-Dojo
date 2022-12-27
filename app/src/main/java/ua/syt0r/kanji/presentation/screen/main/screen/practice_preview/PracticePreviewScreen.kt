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

    val state = viewModel.state

    LaunchedEffect(Unit) {
        viewModel.loadPracticeInfo(practiceId)
    }

    PracticePreviewScreenUI(
        title = practiceTitle,
        state = state,
        onSortSelected = { viewModel.applySortConfig(it) },
        navigateBack = { navigation.navigateBack() },
        navigateToEdit = {
            val configuration = CreatePracticeConfiguration.EditExisting(practiceId)
            navigation.navigateToPracticeCreate(configuration)
        },
        navigateToCharacterInfo = { navigation.navigateToKanjiInfo(it) },
        navigateToPractice = { group, configuration ->
            val writingConfiguration = viewModel.getPracticeConfiguration(group, configuration)
            navigation.navigateToWritingPractice(writingConfiguration)
        }
    )

}
