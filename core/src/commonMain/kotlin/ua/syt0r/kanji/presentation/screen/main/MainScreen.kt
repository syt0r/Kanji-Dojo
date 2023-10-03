package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.VersionChangeDialog

@Composable
fun MainScreen() {
    val navigationState = rememberMainNavigationState()
    MainNavigation(navigationState)

    val viewModel = getMultiplatformViewModel<MainContract.ViewModel>()
    if (viewModel.shouldShowVersionChangeDialog.value) {
        VersionChangeDialog { viewModel.markVersionChangeDialogShown() }
    }

}