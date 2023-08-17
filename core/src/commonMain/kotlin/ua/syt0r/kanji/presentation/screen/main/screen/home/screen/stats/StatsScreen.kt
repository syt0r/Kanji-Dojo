package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats

import androidx.compose.runtime.Composable

@Composable
fun StatsScreen(
    viewModel: StatsScreenContract.ViewModel
) {

    StatsScreenUI(state = viewModel.state)

}