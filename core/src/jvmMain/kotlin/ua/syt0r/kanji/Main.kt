package ua.syt0r.kanji

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import ua.syt0r.kanji.presentation.KanjiDojoApp
import ua.syt0r.kanji.presentation.common.resources.string.EnglishStrings

fun main(args: Array<String>) = application {
    startKoin { loadKoinModules(appModules) }
    Window(
        onCloseRequest = ::exitApplication,
        title = EnglishStrings.appName
    ) {
        KanjiDojoApp()
    }
}
