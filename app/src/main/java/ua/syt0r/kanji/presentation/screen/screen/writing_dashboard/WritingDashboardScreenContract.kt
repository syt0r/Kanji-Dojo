package ua.syt0r.kanji.presentation.screen.screen.writing_dashboard

import androidx.lifecycle.LiveData
import ua.syt0r.kanji.core.user_data.model.PracticeSetInfo

interface WritingDashboardScreenContract {

    interface ViewModel {
        val state: LiveData<State>
    }

    sealed class State {

        object Loading : State()

        data class Loaded(
            val practiceSets: List<PracticeSetInfo>
        ) : State()

    }

}