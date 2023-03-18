package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.PracticeImportScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data.AllImportCategories
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.ui.PracticeImportScreenUI

@Preview
@Composable
private fun LoadingPreview() {
    AppTheme {
        PracticeImportScreenUI(state = rememberUpdatedState(ScreenState.Loading))
    }
}

@Preview
@Composable
private fun LoadedPreview() {
    AppTheme {
        PracticeImportScreenUI(
            state = rememberUpdatedState(ScreenState.Loaded(AllImportCategories))
        )
    }
}
