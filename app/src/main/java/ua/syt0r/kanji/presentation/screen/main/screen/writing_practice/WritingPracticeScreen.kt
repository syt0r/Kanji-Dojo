package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.screen.main.FlexibleScreens
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeConfiguration

@Composable
fun WritingPracticeScreen(
    configuration: WritingPracticeConfiguration,
    mainNavigationState: MainNavigationState
) {
    FlexibleScreens.LocalWritingPracticeScreenContent.current
        .Draw(configuration, mainNavigationState)
}
