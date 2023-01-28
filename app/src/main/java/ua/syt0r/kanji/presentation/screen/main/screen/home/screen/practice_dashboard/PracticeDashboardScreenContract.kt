package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard

import androidx.compose.runtime.State
import ua.syt0r.kanji.core.user_data.model.ReviewedPractice

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
            val practiceSets: List<ReviewedPractice>,
            val shouldShowAnalyticsSuggestion: Boolean
        ) : ScreenState()

    }

}