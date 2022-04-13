package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.data.DashboardScreenState
import javax.inject.Inject

@HiltViewModel
class DashboardScreenViewModel @Inject constructor(
    private val userRepository: UserDataContract.PracticeRepository
) : ViewModel(), DashboardScreenContract.ViewModel {

    override val state = mutableStateOf<DashboardScreenState>(DashboardScreenState.Loading)

    init {
        state.value = DashboardScreenState.Loaded(
            recentPractice = null
        )
    }

}