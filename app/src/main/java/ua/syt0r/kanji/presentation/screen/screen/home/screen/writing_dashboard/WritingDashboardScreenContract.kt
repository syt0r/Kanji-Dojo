package ua.syt0r.kanji.presentation.screen.screen.home.screen.writing_dashboard

import androidx.lifecycle.LiveData
import ua.syt0r.kanji.core.user_data.model.Practice

interface WritingDashboardScreenContract {

    interface ViewModel {

        val state: LiveData<State>

        fun refreshData()
        fun invalidateData()

    }

    sealed class State {

        object Loading : State()

        data class Loaded(
            val practiceSets: List<Practice>
        ) : State()

    }

}