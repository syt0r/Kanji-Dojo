package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.screen.main.FlexibleScreens
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState

@Composable
fun WritingPracticeScreen(
    configuration: MainDestination.Practice.Writing,
    mainNavigationState: MainNavigationState
) {
    FlexibleScreens.LocalWritingPracticeScreenContent.current
        .Draw(configuration, mainNavigationState)
}
