package ua.syt0r.kanji.desktopApp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ua.syt0r.kanji.di.appModules
import ua.syt0r.kanji.presentation.KanjiDojoApp
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.Orientation
import ua.syt0r.kanji.presentation.screen.main.screen.credits.GetCreditLibrariesUseCase

val desktopAppModule = module {
    factory<GetCreditLibrariesUseCase> { JvmGetCreditLibrariesUseCase }
}

fun main(args: Array<String>) = application {

    val koinModuleList = appModules.plus(desktopAppModule)
    startKoin { loadKoinModules(koinModuleList) }

    val windowState = rememberWindowState()

    Window(
        onCloseRequest = { exitApplication() },
        state = windowState,
        title = resolveString { appName },
        icon = painterResource(Res.drawable.windowIcon)
    ) {
        val orientation = when (windowState.size.run { height > width }) {
            true -> Orientation.Portrait
            false -> Orientation.Landscape
        }

        KanjiDojoApp(
            orientation
        )

    }

}
