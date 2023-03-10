package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data

import kotlin.time.Duration

data class PracticeDashboardItem(
    val practiceId: Long,
    val title: String,
    val reviewToNowDuration: Duration?
)