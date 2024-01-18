package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.use_case

import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardItem
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract
import kotlin.time.Duration

class PracticeDashboardApplySortUseCase : PracticeDashboardScreenContract.ApplySortUseCase {

    override fun sort(
        sortByTime: Boolean,
        items: List<PracticeDashboardItem>
    ): List<PracticeDashboardItem> {
        return when {
            sortByTime -> items.sortedBy { it.timeSinceLastPractice ?: Duration.INFINITE }
            else -> items.sortedByDescending { it.position }
        }
    }

}