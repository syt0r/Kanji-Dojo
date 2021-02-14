package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard

import androidx.lifecycle.LiveData
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.data.DashboardScreenData

interface WritingDashboardScreenContract {

    interface ViewModel {
        val state: LiveData<State>
    }

    sealed class State {

        object Loading : State()

        data class Loaded(
            val dashboardScreenData: DashboardScreenData
        ) : State()

        companion object {
            val DEFAULT = Loading
        }

    }

}