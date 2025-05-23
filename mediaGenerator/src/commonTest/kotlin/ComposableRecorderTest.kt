import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.runSkikoComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.Orientation

interface CompactRecorderTestScope {

    @OptIn(ExperimentalTestApi::class)
    @Composable
    fun TestLaunchedEffect(block: suspend ComposeUiTest.(TestRuleRecorder) -> Unit)

}

data class ComposableRecorderTestConfiguration(
    val size: IntSize = IntSize(1080, 1920),
    val density: Density = Density(1.8f),
    val orientation: Orientation = Orientation.Portrait,
    val darkTheme: Boolean = false,
)

@OptIn(ExperimentalTestApi::class)
fun composableRecorderTest(
    configuration: ComposableRecorderTestConfiguration = ComposableRecorderTestConfiguration(),
    autoAdvance: Boolean = false,
    content: @Composable CompactRecorderTestScope.() -> Unit,
) {

    val size: IntSize = configuration.size
    val density: Density = configuration.density
    val orientation: Orientation = configuration.orientation
    val darkTheme: Boolean = configuration.darkTheme

    runSkikoComposeUiTest(
        size = size.toSize(),
        density = density
    ) {

        val blockCompletable =
            CompletableDeferred<suspend ComposeUiTest.(TestRuleRecorder) -> Unit>()

        val compactRecorderTestScope = object : CompactRecorderTestScope {

            @Composable
            override fun TestLaunchedEffect(block: suspend ComposeUiTest.(TestRuleRecorder) -> Unit) {
                LaunchedEffect(Unit) {
                    blockCompletable.complete(block)
                }
            }

        }

        mainClock.autoAdvance = autoAdvance

        setContent {
            key(configuration) {
                AppTheme(
                    useDarkTheme = darkTheme,
                    orientation = orientation
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        content.invoke(compactRecorderTestScope)
                    }
                }
            }
        }

        val recorder = JavaCvTestRuleRecorder(this)

        runBlocking {
            mainClock.advanceTimeByFrame()
            blockCompletable.await().invoke(this@runSkikoComposeUiTest, recorder)
        }

    }

}

@OptIn(ExperimentalTestApi::class)
fun composableRecorderTest(
    configurationList: List<ComposableRecorderTestConfiguration>,
    autoAdvance: Boolean = false,
    content: @Composable CompactRecorderTestScope.(ComposableRecorderTestConfiguration) -> Unit,
) {
    configurationList.forEach {
        composableRecorderTest(it, autoAdvance) {
            content(it)
        }
    }
}

fun SemanticsNodeInteractionsProvider.nodeExists(matcher: SemanticsMatcher): Boolean {
    return onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()
}

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.setAppContent(
    useDarkTheme: Boolean = false,
    orientation: Orientation = Orientation.Portrait,
    content: @Composable (() -> Unit),
) {
    setContent {
        AppTheme(
            useDarkTheme = useDarkTheme,
            orientation = orientation
        ) {
            Surface {
                content()
            }
        }
    }
}
