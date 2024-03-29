package ua.syt0r.kanji.presentation.screen.main.screen.home.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import ua.syt0r.kanji.presentation.common.resources.string.StringResolveScope

enum class HomeScreenTab(
    val imageVector: ImageVector,
    val titleResolver: StringResolveScope<String>
) {

    PracticeDashboard(
        imageVector = Icons.Default.List,
        titleResolver = { home.dashboardTabLabel },
    ),
    Stats(
        imageVector = Icons.Default.QueryStats,
        titleResolver = { home.statsTabLabel }
    ),
    Search(
        imageVector = Icons.Default.Search,
        titleResolver = { home.searchTabLabel }
    ),
    Settings(
        imageVector = Icons.Outlined.Settings,
        titleResolver = { home.settingsTabLabel }
    );

    companion object {
        val Default = PracticeDashboard
        val VisibleTabs = listOf(PracticeDashboard, Stats, Search, Settings)
    }

}