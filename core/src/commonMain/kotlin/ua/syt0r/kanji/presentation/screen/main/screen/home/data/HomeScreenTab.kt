package ua.syt0r.kanji.presentation.screen.main.screen.home.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import ua.syt0r.kanji.presentation.common.resources.icon.ExtraIcons
import ua.syt0r.kanji.presentation.common.resources.icon.Translate
import ua.syt0r.kanji.presentation.common.resources.string.StringResolveScope

enum class HomeScreenTab(
    val imageVector: ImageVector,
    val titleResolver: StringResolveScope
) {

    PRACTICE_DASHBOARD(
        imageVector = ExtraIcons.Translate,
        titleResolver = { homeTabDashboard },
    ),
    SEARCH(
        imageVector = Icons.Default.Search,
        titleResolver = { homeTabSearch }
    ),
    SETTINGS(
        imageVector = Icons.Outlined.Settings,
        titleResolver = { homeTabSettings }
    );

    companion object {
        val Default = PRACTICE_DASHBOARD
    }

}