package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.screen.MainContract
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.ui.DashboardScreenUI

@Composable
fun DashboardScreen(navigation: MainContract.Navigation) {

    DashboardScreenUI(
        onWritingOptionSelected = {  },
        onDismissNotificationNotice = {}
    )

}