import androidx.compose.runtime.State
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import org.junit.Test
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ua.syt0r.kanji.core.sync.SyncFeatureState
import ua.syt0r.kanji.core.sync.SyncManager
import ua.syt0r.kanji.di.appModules
import ua.syt0r.kanji.mediaGenerator.ScreenshotColumn
import ua.syt0r.kanji.presentation.common.ScreenLetterPracticeType
import ua.syt0r.kanji.presentation.common.ScreenVocabPracticeType
import ua.syt0r.kanji.presentation.common.ui.Orientation
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainScreen
import ua.syt0r.kanji.presentation.screen.main.screen.deck_picker.DeckPickerScreen
import ua.syt0r.kanji.presentation.screen.main.screen.deck_picker.data.DeckPickerScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.home.HomeScreenTab
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.dashboard_common.DeckDashboardListItemHeaderButtonTestTag
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.CharacterInputData
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.DefaultLetterPracticeScreenContent
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.LetterPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.WritingPracticeHintMode
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.VocabPracticeScreen
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.VocabPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabPracticeScreenConfiguration


@OptIn(ExperimentalTestApi::class)
class ScreenshotTests {

    private companion object {

        val portraitPhone = ComposableRecorderTestConfiguration(
            size = IntSize(1080, 1920),
            density = Density(1.9f),
            orientation = Orientation.Portrait,
            darkTheme = false
        )

        val landscapePhone = ComposableRecorderTestConfiguration(
            size = IntSize(1920, 1080),
            density = Density(1.1f),
            orientation = Orientation.Landscape,
            darkTheme = false
        )

        val configurations = listOf(portraitPhone, landscapePhone)

    }

    init {
        val testModule = module {
            single<SyncManager> {
                mockk {
                    every { state } returns MutableStateFlow(SyncFeatureState.Disabled)
                }
            }
        }
        startKoin { modules(appModules.plus(testModule)) }
    }

    suspend inline fun <reified T> State<*>.waitForFirstInstance(): T {
        return snapshotFlow { value }.filterIsInstance<T>().first()
    }

    suspend inline fun <reified T> StateFlow<*>.waitForFirstInstance(): T {
        return filterIsInstance<T>().first()
    }

    @Test
    fun screenshot1() = composableRecorderTest(
        configurations,
        autoAdvance = true
    ) { configuration ->

        val viewModel = getMultiplatformViewModel<LetterPracticeScreenContract.ViewModel>()

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

        TestLaunchedEffect {

            waitForIdle()

            viewModel.state
                .waitForFirstInstance<LetterPracticeScreenContract.ScreenState.Configuring>()
                .let { it.configuration as LetterPracticeConfiguration.Writing }
                .hintMode.value = WritingPracticeHintMode.All

            onNodeWithText("Start").performClick()

            val reviewState = viewModel.state
                .waitForFirstInstance<LetterPracticeScreenContract.ScreenState.Review>()
                .let { it.reviewState as LetterPracticeReviewState.Writing }
                .studyWriterState!!

            reviewState.strokes.dropLast(1).forEach {
                reviewState.submit(CharacterInputData.SingleStroke(it, it))
            }

            awaitIdle()

            if (configuration.orientation == Orientation.Landscape) {
                onNode(hasText("Expressions", substring = true)).performClick()
                awaitIdle()
            }

            mainClock.advanceTimeBy(1000, true)

            it.screenshot("screenshot_1_${configuration.orientation.name.lowercase()}")
        }

    }


    @Test
    fun screenshot2() = composableRecorderTest(
        configurations,
        autoAdvance = true
    ) { configuration ->

        val viewModel = getMultiplatformViewModel<LetterPracticeScreenContract.ViewModel>()

        ScreenshotColumn(
            title = """
                Memorize Kanji meanings
                and readings with 
                vocabulary samples
            """.trimIndent()
        ) {
            DefaultLetterPracticeScreenContent(
                configuration = LetterPracticeScreenConfiguration(
                    mapOf("番" to -1),
                    ScreenLetterPracticeType.Reading
                ),
                mainNavigationState = mockk(),
                viewModel = viewModel
            )
        }

        TestLaunchedEffect {

            waitForIdle()

            viewModel.state
                .waitForFirstInstance<LetterPracticeScreenContract.ScreenState.Configuring>()
                .let { it.configuration as LetterPracticeConfiguration.Reading }

            onNodeWithText("Start").performClick()

            viewModel.state.waitForFirstInstance<LetterPracticeScreenContract.ScreenState.Review>()

            onNode(hasText("Show Answer")).performClick()

            awaitIdle()

            it.screenshot("screenshot_2_${configuration.orientation.name.lowercase()}")
        }


    }

    @Test
    fun screenshot3() = composableRecorderTest(
        configurations,
        autoAdvance = true
    ) { configuration ->

        val viewModel = getMultiplatformViewModel<VocabPracticeScreenContract.ViewModel>()

        ScreenshotColumn(
            title = """
                    Various modes to 
                    study vocabulary
                """.trimIndent()
        ) {
            VocabPracticeScreen(
                configuration = VocabPracticeScreenConfiguration(
                    mapOf(1L to -1),
                    ScreenVocabPracticeType.ReadingPicker
                ),
                mainNavigationState = mockk(),
                viewModel = viewModel
            )
        }

        TestLaunchedEffect {

            waitForIdle()

            viewModel.state
                .waitForFirstInstance<VocabPracticeScreenContract.ScreenState.Configuration>()

            onNodeWithText("Start").performClick()

            viewModel.state.waitForFirstInstance<VocabPracticeScreenContract.ScreenState.Review>()

            awaitIdle()

            it.screenshot("screenshot_3_${configuration.orientation.name.lowercase()}")
        }

    }


    @Test
    fun screenshot4() = composableRecorderTest(
        configurations,
        autoAdvance = true
    ) { configuration ->

        ScreenshotColumn(
            title = """
                    Use Spaced Repetition
                    System to learn efficiently
                """.trimIndent()
        ) {
            MainScreen(koinInject())
        }

        TestLaunchedEffect {
            waitForIdle()
            onNodeWithTag(HomeScreenTab.LettersDashboard.buttonTestTag).performClick()
            waitForIdle()
            onAllNodesWithTag(DeckDashboardListItemHeaderButtonTestTag).run {
                listOf(get(1), get(2)).forEach {
                    it.performTouchInput { down(0, Offset(10f, 10f)); }
                    mainClock.advanceTimeByFrame()
                    it.performTouchInput { up() }
                }
            }
            waitForIdle()
            mainClock.advanceTimeBy(300, true)

            it.screenshot("screenshot_4_${configuration.orientation.name.lowercase()}")
        }

    }

    @Test
    fun screenshot5() = composableRecorderTest(
        configurations,
        autoAdvance = true
    ) { configuration ->

        ScreenshotColumn(
            title = """
                    Study freely with no locked 
                    or paid content — this app 
                    is open source
                """.trimIndent()
        ) {
            DeckPickerScreen(
                configuration = DeckPickerScreenConfiguration.Letters,
                mainNavigationState = mockk()
            )
        }

        TestLaunchedEffect {

            waitForIdle()

            onNodeWithText("Grade").performClick()

            waitForIdle()

            it.screenshot("screenshot_5_${configuration.orientation.name.lowercase()}")
        }

    }

}