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
        titleResolver = { homeTabDashboard },
    ),
    Stats(
        imageVector = Icons.Default.QueryStats,
        titleResolver = { "Stats" }
    ),
    Search(
        imageVector = Icons.Default.Search,
        titleResolver = { homeTabSearch }
    ),
    Settings(
        imageVector = Icons.Outlined.Settings,
        titleResolver = { homeTabSettings }
    );

    companion object {
        val Default = PracticeDashboard
    }

}