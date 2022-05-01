package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.data

import ua.syt0r.kanji.core.user_data.model.RecentPractice

sealed class DashboardScreenState {

    object Loading : DashboardScreenState()

    class Loaded(
        val recentPractice: RecentPractice?
    ) : DashboardScreenState()

}