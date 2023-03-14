package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState

object FdroidWritingPracticeScreenContent : WritingPracticeScreenContract.ScreenContent {

    @Composable
    override fun Draw(
        configuration: MainDestination.Practice.Writing,
        mainNavigationState: MainNavigationState,
    ) {
        FdroidWritingPracticeScreen(configuration, mainNavigationState, getMultiplatformViewModel())
    }

}