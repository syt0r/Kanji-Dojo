package ua.syt0r.kanji.presentation.screen.main.screen.backup

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState

@Composable
fun BackupScreen(
    mainNavigationState: MainNavigationState,
    viewModel: BackupContract.ViewModel = getMultiplatformViewModel()
) {

    BackupScreenUI(
        onUpButtonClick = { mainNavigationState.navigateBack() },
        createBackup = { viewModel.createBackup(it) }
    )

}