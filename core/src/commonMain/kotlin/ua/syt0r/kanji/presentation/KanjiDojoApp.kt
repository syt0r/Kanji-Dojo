package ua.syt0r.kanji.presentation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import ua.syt0r.kanji.core.logger.Logger.getKoin
import ua.syt0r.kanji.core.theme_manager.LocalThemeManager
import ua.syt0r.kanji.core.theme_manager.ThemeManager
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.MainScreen

@Composable
fun KanjiDojoApp(
    themeManager: ThemeManager = getKoin().get()
) {
    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        AppTheme {
            Surface {
                MainScreen()
            }
        }
    }
}
