package ua.syt0r.kanji.presentation.screen.screen.practice_import

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.practice_import.ui.WritingPracticeImportScreenUI

@Composable
fun WritingPracticeImportScreen(
    mainNavigation: MainContract.Navigation,
    viewModel: PracticeImportScreenContract.ViewModel = hiltViewModel<PracticeImportViewModel>()
) {

    val screenState = viewModel.state.value

    WritingPracticeImportScreenUI(
        screenState = screenState,
        onUpButtonClick = { mainNavigation.navigateBack() },
        onItemSelected = {
            mainNavigation.navigateToPracticeCreate(
                configuration = CreatePracticeConfiguration.Import(
                    title = it.title,
                    classification = it.classification
                )
            )
        }
    )

}
