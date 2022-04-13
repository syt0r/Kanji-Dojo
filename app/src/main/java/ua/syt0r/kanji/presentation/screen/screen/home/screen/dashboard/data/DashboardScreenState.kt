package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.data

sealed class DashboardScreenState {

    object Loading : DashboardScreenState()

    class Loaded(
        recentPractice: RecentPractice?
    ) : DashboardScreenState()

}