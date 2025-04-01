package ua.syt0r.kanji.mediaGenerator

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import org.koin.compose.koinInject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.theme_manager.LocalThemeManager
import ua.syt0r.kanji.di.appModules
import ua.syt0r.kanji.presentation.common.ScreenLetterPracticeType
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.DefaultLetterPracticeScreenContent
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.LetterPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.LetterPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeScreenConfiguration


@Composable
fun ApplicationScope.RecordingAppWindow(
    windowSize: DpSize,
    content: @Composable RecordingState.() -> Unit
) {

    val windowState = rememberWindowState(size = windowSize)
    val density = LocalDensity.current

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        undecorated = true
    ) {
        CompositionLocalProvider(
            LocalThemeManager provides koinInject()
        ) {
            AppTheme(useDarkTheme = false) {
                Surface { RecordingBox { content() } }
            }
        }

    }

}

fun main() {

    startKoin { loadKoinModules(appModules) }

    application {

        RecordingAppWindow(
            windowSize = DpSize(1200.dp, 800.dp)
        ) {

            val viewModel: LetterPracticeScreenContract.ViewModel = getMultiplatformViewModel()

            ScreenshotColumn(
                title = """
                        Learn how to write more 
                        than 6000 characters
                    """.trimIndent()
            ) {
                DefaultLetterPracticeScreenContent(
                    configuration = LetterPracticeScreenConfiguration(
                        mapOf("字" to -1),
                        ScreenLetterPracticeType.Writing
                    ),
                    mainNavigationState = mockk(),
                    viewModel = viewModel
                )
            }

            LaunchedEffect(Unit) {
                Logger.d("starting")
                viewModel.state.waitForFirstOf { it is ScreenState.Configuring }
                viewModel.configure()
                viewModel.state.waitForFirstOf { it is ScreenState.Review }
                delay(600)
                captureScreenshot("mobile1.png")
                stopVideoCapture()
                exitApplication()
            }

        }

    }

}

suspend fun <T> State<T>.waitForFirstOf(predicate: (T) -> Boolean) = snapshotFlow { value }
    .filter { predicate(it) }
    .first()
