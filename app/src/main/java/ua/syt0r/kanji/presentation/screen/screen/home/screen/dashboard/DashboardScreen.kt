package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.DashboardScreenUI

@Composable
fun DashboardScreen(
    navigation: MainContract.Navigation,
    viewModel: DashboardScreenViewModel = hiltViewModel()
) {

    val state by viewModel.state

    DashboardScreenUI(
        screenState = state,
        onWritingOptionSelected = { }
    )

}