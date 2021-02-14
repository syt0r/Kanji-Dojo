package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.WritingDashboardScreenContract.State


class WritingDashboardViewModel : ViewModel(), WritingDashboardScreenContract.ViewModel {

    override val state = MutableLiveData<State>(State.Loading)

    init {

    }

}