package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeConfiguration

@Composable
fun ReadingPracticeScreen(
    navigationState: MainNavigationState,
    configuration: ReadingPracticeConfiguration
) {

    val viewModel = hiltViewModel<ReadingPracticeViewModel>()

    LaunchedEffect(Unit) {
        viewModel.initialize(configuration)
        viewModel.reportScreenShown(configuration)
    }

    ReadingPracticeScreenUI(
        state = viewModel.state,
        onUpButtonClick = { navigationState.navigateBack() },
        onOptionSelected = { viewModel.select(it) },
        onFinishButtonClick = { navigationState.navigateBack() }
    )

}
