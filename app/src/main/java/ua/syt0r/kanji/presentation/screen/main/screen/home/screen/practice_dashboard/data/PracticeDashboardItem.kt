package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data

import java.time.LocalDateTime

data class PracticeDashboardItem(
    val practiceId: Long,
    val title: String,
    val reviewTime: LocalDateTime?
)