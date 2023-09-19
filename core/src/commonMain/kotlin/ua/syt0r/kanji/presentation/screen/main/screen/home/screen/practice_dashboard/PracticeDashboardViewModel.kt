package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.app_state.AppStateManager
import ua.syt0r.kanji.core.app_state.DailyGoalConfiguration
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract.ScreenState


class PracticeDashboardViewModel(
    private val viewModelScope: CoroutineScope,
    loadDataUseCase: PracticeDashboardScreenContract.LoadDataUseCase,
    private val appStateManager: AppStateManager,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analyticsManager: AnalyticsManager
) : PracticeDashboardScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    init {
        loadDataUseCase.load()
            .onEach { state.value = it }
            .launchIn(viewModelScope)
    }

    override fun updateDailyGoal(configuration: DailyGoalConfiguration) {
        viewModelScope.launch {
            userPreferencesRepository.setDailyLearnLimit(configuration.learnLimit)
            userPreferencesRepository.setDailyReviewLimit(configuration.reviewLimit)
            appStateManager.invalidate()
        }
    }

    override fun reportScreenShown() {
        analyticsManager.setScreen("practice_dashboard")
    }

}