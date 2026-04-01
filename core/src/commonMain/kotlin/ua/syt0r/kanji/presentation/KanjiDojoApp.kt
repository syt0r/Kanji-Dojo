package ua.syt0r.kanji.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import ua.syt0r.kanji.core.theme_manager.ThemeManager
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.Orientation
import ua.syt0r.kanji.presentation.screen.main.MainScreen
import ua.syt0r.kanji.presentation.screen.main.features.DeepLinkHandler

@Composable
fun KanjiDojoApp(
    windowSizeClass: WindowSizeClass,
    deepLinkHandler: DeepLinkHandler = koinInject(),
    themeManager: ThemeManager = koinInject()
) {

    val orientation = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> Orientation.Landscape
        else -> Orientation.Portrait
    }

    AppTheme(
        useDarkTheme = themeManager.isDarkTheme,
        useAmoledTheme = themeManager.isAmoledTheme,
        orientation = orientation
    ) {
        Surface {
            Box(
                modifier = Modifier.safeDrawingPadding()
            ) {
                MainScreen(deepLinkHandler)
            }
        }
    }

}
