package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract.ScreenState


class PracticeDashboardViewModel(
    private val viewModelScope: CoroutineScope,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analyticsManager: AnalyticsManager,
    private val loadDataUseCase: PracticeDashboardScreenContract.LoadDataUseCase
) : PracticeDashboardScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    init {
        loadDataUseCase.load()
            .onEach { state.value = it }
            .launchIn(viewModelScope)
// TODO       shouldShowAnalyticsSuggestion = it.lastData.decks.isNotEmpty() &&
//                it.lastData.decks.any { it.timeSinceLastReview != null } &&
//                userPreferencesRepository.getShouldShowAnalyticsSuggestion() &&
//                !userPreferencesRepository.getAnalyticsEnabled(),
    }

    override fun refreshData() {

    }

    override fun enableAnalytics() {
        viewModelScope.launch {
            userPreferencesRepository.setAnalyticsEnabled(true)
            analyticsManager.setAnalyticsEnabled(true)
        }
    }

    override fun dismissAnalyticsSuggestion() {
        val currentState = state.value as ScreenState.Loaded
//        state.value = currentState.copy(shouldShowAnalyticsSuggestion = false)
        viewModelScope.launch { userPreferencesRepository.setShouldShowAnalyticsSuggestion(false) }
    }

    override fun reportScreenShown() {
        analyticsManager.setScreen("practice_dashboard")
    }

}