package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.data

data class DashboardScreenData(
    val progressList: List<DashboardProgressItemData>,
    val kanjiToReview: Int
)

data class DashboardProgressItemData(
    val text: String,
    val max: Int,
    val current: Int
)