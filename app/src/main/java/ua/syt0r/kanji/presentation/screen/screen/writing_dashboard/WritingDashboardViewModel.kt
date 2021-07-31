package ua.syt0r.kanji.presentation.screen.screen.writing_dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.writing_dashboard.WritingDashboardScreenContract.State
import javax.inject.Inject

@HiltViewModel
class WritingDashboardViewModel @Inject constructor(
    private val usedDataRepository: UserDataContract.WritingRepository
) : ViewModel(), WritingDashboardScreenContract.ViewModel {

    override val state = MutableLiveData<State>(State.Loading)

    init {
        viewModelScope.launch(Dispatchers.IO) { fetchData() }
    }

    private suspend fun fetchData() {
        state.postValue(
            State.Loaded(usedDataRepository.getAllPracticeSets())
        )
    }

}