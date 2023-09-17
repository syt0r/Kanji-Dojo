package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.DailyIndicatorData
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.PracticeDashboardItem

interface PracticeDashboardScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun refreshData()
        fun enableAnalytics()
        fun dismissAnalyticsSuggestion()

        fun reportScreenShown()

    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Loaded(
            val practiceSets: List<PracticeDashboardItem>,
            val dailyIndicatorData: DailyIndicatorData
        ) : ScreenState()

    }

    interface LoadDataUseCase {
        fun load(): Flow<ScreenState>
    }

}