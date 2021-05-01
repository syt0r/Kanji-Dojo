package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStoreContract
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.WritingDashboardScreenContract.State


class WritingDashboardViewModel(
    private val kanjiDataStore: KanjiDataStoreContract.DataStore
) : ViewModel(), WritingDashboardScreenContract.ViewModel {

    override val state = MutableLiveData<State>(State.Loading)

    init {
        viewModelScope.launch(Dispatchers.IO) { fetchData() }
    }

    private fun fetchData() {
        val classifications = kanjiDataStore.getKanjiClassifications()
        state.postValue(State.Loaded(classifications))
    }

}