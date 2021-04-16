package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.data

import ua.syt0r.kanji.ui.common.kanji.KanjiDrawData

data class WritingDashboardScreenData(
    val categories: List<WritingDashboardScreenCategory>
)

data class WritingDashboardScreenCategory(
    val title: String,
    val items: List<WritingDashboardScreenItem>
)

sealed class WritingDashboardScreenItem {

    data class SingleItem(
        val title: String,
        val previewKanji: KanjiDrawData
    ) : WritingDashboardScreenItem()

    data class ItemGroup(
        val title: String,
        val previewKanji: KanjiDrawData,
        val items: List<SingleItem>
    ) : WritingDashboardScreenItem()

}