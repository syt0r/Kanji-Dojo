package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract.ScreenState
import javax.inject.Inject

@HiltViewModel
class PracticeDashboardViewModel @Inject constructor(
    private val userPreferencesRepository: UserDataContract.PreferencesRepository,
    private val usedDataRepository: UserDataContract.PracticeRepository
) : ViewModel(), PracticeDashboardScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun refreshData() {
        Logger.logMethod()
        viewModelScope.launch {
            state.value = ScreenState.Loading

            val practiceSets = withContext(Dispatchers.IO) {
                usedDataRepository.getAllPractices()
            }

            state.value = ScreenState.Loaded(
                practiceSets = practiceSets,
                shouldShowAnalyticsSuggestion = practiceSets.isNotEmpty() &&
                        userPreferencesRepository.getShouldShowAnalyticsSuggestion()
            )

        }
    }

    override fun dismissAnalyticsSuggestion() {
        val currentState = state.value as ScreenState.Loaded
        state.value = currentState.copy(shouldShowAnalyticsSuggestion = false)
        viewModelScope.launch { userPreferencesRepository.setShouldShowAnalyticsSuggestion(false) }
    }

}