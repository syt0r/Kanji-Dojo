package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.data.DashboardScreenState

interface DashboardScreenContract {

    interface ViewModel {
        val state: State<DashboardScreenState>
    }

}