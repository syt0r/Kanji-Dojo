package ua.syt0r.kanji.presentation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.MainScreen

@Composable
fun KanjiDojoApp() {
    AppTheme {
        Surface {
            MainScreen()
        }
    }
}
