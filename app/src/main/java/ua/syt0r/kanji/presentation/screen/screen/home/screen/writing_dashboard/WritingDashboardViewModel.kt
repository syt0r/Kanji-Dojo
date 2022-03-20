package ua.syt0r.kanji.presentation.screen.screen.home.screen.writing_dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.home.screen.writing_dashboard.WritingDashboardScreenContract.State
import javax.inject.Inject

@HiltViewModel
class WritingDashboardViewModel @Inject constructor(
    private val usedDataRepository: UserDataContract.WritingRepository
) : ViewModel(), WritingDashboardScreenContract.ViewModel {

    override val state = MutableLiveData<State>(State.Loading)
    private var shouldRefreshList: Boolean = true

    init {
        loadData()
    }

    override fun refreshData() {
        if (state.value !is State.Loading && shouldRefreshList) {
            state.value = State.Loading
            loadData()
        }
    }

    override fun invalidateData() {
        shouldRefreshList = true
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val practiceSets = usedDataRepository.getAllPracticeSets()
            state.postValue(State.Loaded(practiceSets))
            shouldRefreshList = false
        }
    }

}