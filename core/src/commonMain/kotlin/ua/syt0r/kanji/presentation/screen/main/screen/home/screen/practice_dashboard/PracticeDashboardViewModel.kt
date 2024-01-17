package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
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

    private lateinit var listMode: MutableStateFlow<PracticeDashboardListMode>

    init {
        loadDataUseCase.load()
            .onEach {
                listMode = MutableStateFlow(PracticeDashboardListMode.Default(it.items))
                state.value = ScreenState.Loaded(
                    mode = listMode,
                    dailyIndicatorData = it.dailyIndicatorData
                )
            }
            .launchIn(viewModelScope)
    }

    override fun updateDailyGoal(configuration: DailyGoalConfiguration) {
        viewModelScope.launch {
            userPreferencesRepository.setDailyLimitEnabled(configuration.enabled)
            userPreferencesRepository.setDailyLearnLimit(configuration.learnLimit)
            userPreferencesRepository.setDailyReviewLimit(configuration.reviewLimit)
            appStateManager.invalidate()
            analyticsManager.sendEvent("daily_goal_update") {
                put("enabled", configuration.enabled)
                put("learn_limit", configuration.learnLimit)
                put("review_limit", configuration.reviewLimit)
            }
        }
    }

    override fun enablePracticeMergeMode() {
        listMode.value = PracticeDashboardListMode.MergeMode(
            items = listMode.value.items,
            selected = mutableStateOf(emptySet()),
            title = mutableStateOf("")
        )
    }

    override fun merge(data: PracticeMergeRequestData) {
        state.value = ScreenState.Loading
    }

    override fun enablePracticeReorderMode() {
        val items = listMode.value.items
        listMode.value = PracticeDashboardListMode.SortMode(
            items = items,
            reorderedList = mutableStateOf(items),
            sortByReviewTime = mutableStateOf(false)
        )
    }

    override fun reorder(data: PracticeReorderRequestData) {
        state.value = ScreenState.Loading
    }

    override fun enableDefaultMode() {
        listMode.value = PracticeDashboardListMode.Default(
            items = listMode.value.items
        )
    }

    override fun reportScreenShown() {
        analyticsManager.setScreen("practice_dashboard")
    }

}