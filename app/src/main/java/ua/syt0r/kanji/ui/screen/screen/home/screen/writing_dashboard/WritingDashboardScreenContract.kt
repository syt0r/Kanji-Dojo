package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard

import androidx.lifecycle.LiveData
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.data.WritingDashboardScreenData

interface WritingDashboardScreenContract {

    interface ViewModel {
        val state: LiveData<State>
    }

    sealed class State {

        object Loading : State()

        data class Loaded(
            val screenData: WritingDashboardScreenData
        ) : State()

        companion object {
            val DEFAULT = Loading
        }

    }

}