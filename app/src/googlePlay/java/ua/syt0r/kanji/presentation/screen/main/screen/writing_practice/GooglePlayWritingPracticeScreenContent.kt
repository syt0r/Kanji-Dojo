package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration

object GooglePlayWritingPracticeScreenContent : WritingPracticeScreenContract.ScreenContent {

    @Composable
    override fun Draw(
        configuration: WritingPracticeConfiguration,
        mainNavigationState: MainNavigationState,
    ) {
        GooglePlayWritingPracticeScreen(configuration, mainNavigationState)
    }

}