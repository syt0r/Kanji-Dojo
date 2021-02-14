package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import org.koin.androidx.compose.getViewModel
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.ui.WritingDashboardScreenContent

@Composable
fun WritingDashboardScreen(
    viewModel: WritingDashboardScreenContract.ViewModel = getViewModel<WritingDashboardViewModel>()
) {

    WritingDashboardScreenContent(
        state = viewModel.state.observeAsState(initial = WritingDashboardScreenContract.State.DEFAULT),
    )

}