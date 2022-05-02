package ua.syt0r.kanji.presentation.screen.screen.home.screen.dashboard.data

import ua.syt0r.kanji.core.user_data.model.ReviewedPractice

sealed class DashboardScreenState {

    object Loading : DashboardScreenState()

    class Loaded(
        val reviewedPractice: ReviewedPractice?
    ) : DashboardScreenState()

}