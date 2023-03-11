package ua.syt0r.kanji.presentation.screen.main.screen.practice_import

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.common.rememberUrlHandler
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.ui.PracticeImportScreenUI


@Composable
fun PracticeImportScreen(
    mainNavigationState: MainNavigationState,
    viewModel: PracticeImportScreenContract.ViewModel = hiltViewModel<PracticeImportViewModel>()
) {

    LaunchedEffect(Unit) { viewModel.reportScreenShown() }

    val urlHandler = rememberUrlHandler()

    PracticeImportScreenUI(
        state = viewModel.state,
        onUpButtonClick = { mainNavigationState.navigateBack() },
        onItemSelected = { classification, title ->
            mainNavigationState.navigate(
                MainDestination.CreatePractice.Import(
                    title = title,
                    classification = classification
                )
            )
        },
        onLinkClick = { url -> urlHandler.openInBrowser(url) }
    )

}
