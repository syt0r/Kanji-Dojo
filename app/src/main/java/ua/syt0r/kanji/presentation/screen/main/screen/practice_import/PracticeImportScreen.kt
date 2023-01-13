package ua.syt0r.kanji.presentation.screen.main.screen.practice_import

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.common.openUrl
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.CreatePracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.ui.PracticeImportScreenUI


@Composable
fun PracticeImportScreen(
    mainNavigationState: MainNavigationState,
    viewModel: PracticeImportScreenContract.ViewModel = hiltViewModel<PracticeImportViewModel>()
) {

    val screenState = viewModel.state.value

    val context = LocalContext.current

    PracticeImportScreenUI(
        screenState = screenState,
        onUpButtonClick = { mainNavigationState.navigateBack() },
        onItemSelected = { classification, title ->
            mainNavigationState.navigateToPracticeCreate(
                configuration = CreatePracticeConfiguration.Import(
                    title = title,
                    classification = classification
                )
            )
        },
        onLinkClick = { url -> context.openUrl(url) }
    )

}
