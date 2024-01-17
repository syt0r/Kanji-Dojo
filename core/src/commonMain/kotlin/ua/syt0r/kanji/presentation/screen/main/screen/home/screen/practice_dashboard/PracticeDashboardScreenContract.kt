package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard

import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ua.syt0r.kanji.core.app_state.DailyGoalConfiguration

interface PracticeDashboardScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun updateDailyGoal(configuration: DailyGoalConfiguration)

        fun enablePracticeMergeMode()
        fun merge(data: PracticeMergeRequestData)

        fun enablePracticeReorderMode()
        fun reorder(data: PracticeReorderRequestData)

        fun enableDefaultMode()

        fun reportScreenShown()

    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Loaded(
            val mode: StateFlow<PracticeDashboardListMode>,
            val dailyIndicatorData: DailyIndicatorData
        ) : ScreenState()

    }

    interface LoadDataUseCase {
        fun load(): Flow<PracticeDashboardScreenData>
    }

}