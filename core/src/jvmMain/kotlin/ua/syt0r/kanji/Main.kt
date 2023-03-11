package ua.syt0r.kanji

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import ua.syt0r.kanji.presentation.common.resources.string.EnglishStrings
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.MultiplatformMainNavigation
import ua.syt0r.kanji.presentation.screen.main.rememberMultiplatformMainNavigationState

fun main(args: Array<String>) = application {
    startKoin { loadKoinModules(appModules) }
    Window(
        onCloseRequest = ::exitApplication,
        title = EnglishStrings.appName
    ) {
        App()
    }
}

@Composable
private fun App() {
    AppTheme(false) {
        val navState = rememberMultiplatformMainNavigationState()
        MultiplatformMainNavigation(navState)
    }
}
