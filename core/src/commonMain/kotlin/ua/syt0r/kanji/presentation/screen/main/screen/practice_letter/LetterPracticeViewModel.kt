package ua.syt0r.kanji.presentation.screen.main.screen.practice_letter

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.japanese.KanaReading
import ua.syt0r.kanji.core.tts.KanaTtsManager
import ua.syt0r.kanji.core.tts.WordTtsManager
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.CharacterWritingProgress
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeAnswer
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.LetterPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeItemData
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeQueueState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeSummaryItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.use_case.GetLetterPracticeConfigurationUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.use_case.GetLetterPracticeQueueDataUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.use_case.GetLetterPracticeReviewStateUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.use_case.UpdateLetterPracticeConfigurationUseCase
class LetterPracticeViewModel(
    private val viewModelScope: CoroutineScope,
    private val getConfigurationUseCase: GetLetterPracticeConfigurationUseCase,
    private val updateConfigurationUseCase: UpdateLetterPracticeConfigurationUseCase,
    private val getQueueDataUseCase: GetLetterPracticeQueueDataUseCase,
    private val practiceQueue: LetterPracticeQueue,
    private val getReviewStateUseCase: GetLetterPracticeReviewStateUseCase,
    private val analyticsManager: AnalyticsManager,
    private val kanaTtsManager: KanaTtsManager,
    private val wordTtsManager: WordTtsManager
) : LetterPracticeScreenContract.ViewModel {

    private lateinit var configuration: LetterPracticeScreenConfiguration

    private val _state = mutableStateOf<ScreenState>(ScreenState.Loading)
    override val state: State<ScreenState> = _state

    private val _wordTtsUnavailableMessage = mutableStateOf<String?>(null)
    override val wordTtsUnavailableMessage: State<String?> = _wordTtsUnavailableMessage

    override fun initialize(configuration: LetterPracticeScreenConfiguration) {
        if (this::configuration.isInitialized) return
        this.configuration = configuration

        viewModelScope.launch {
            _state.value = ScreenState.Configuring(
                configuration = getConfigurationUseCase(configuration)
            )
        }

        viewModelScope.launch {
            if (!wordTtsManager.isAvailable()) {
                _wordTtsUnavailableMessage.value = wordTtsManager.unavailableMessage
            }
        }
    }

    override fun configure() {
        val configurationState = _state.value as ScreenState.Configuring
        _state.value = ScreenState.Loading

        viewModelScope.launch {

            updateConfigurationUseCase(configurationState.configuration)

            practiceQueue.initialize(
                items = getQueueDataUseCase(configurationState.configuration)
            )

            practiceQueue.state
                .onEach {
                    when (it) {
                        LetterPracticeQueueState.Loading -> {
                            _state.value = ScreenState.Loading
                        }

                        is LetterPracticeQueueState.Review -> {
                            val reviewState = it.toScreenState()
                            _state.value = reviewState

                            reviewState.kanaAutoReadFlow()
                                .onEach { speakKana(it) }
                                .launchIn(viewModelScope)

                            reviewState.kanjiAutoReadFlow()
                                .onEach { speakWord(it) }
                                .launchIn(viewModelScope)
                        }

                        is LetterPracticeQueueState.Summary -> {
                            _state.value = it.toScreenState()
                        }
                    }
                }
                .launchIn(this)
        }

        reportConfiguration(configurationState)
    }

    override fun submitAnswer(answer: PracticeAnswer) {
        // Writing mode already speaks as soon as the drawing is verified (see
        // kanjiAutoReadFlow/kanaAutoReadFlow below); Reading mode has no such moment, so it
        // speaks here instead, right when the user submits their self-graded answer.
        val reviewState = (state.value as? ScreenState.Review)?.reviewState
        if (reviewState is LetterPracticeReviewState.Reading &&
            reviewState.itemData is LetterPracticeItemData.KanjiReadingData &&
            reviewState.layout.kanaAutoPlay.value
        ) {
            reviewState.itemData.primaryReadingForSpeech?.let { speakWord(it) }
        }
        viewModelScope.launch { practiceQueue.submitAnswer(answer) }
    }

    override fun speakKana(reading: KanaReading) {
        viewModelScope.launch { kanaTtsManager.speak(reading) }
    }

    override fun speakWord(word: String) {
        viewModelScope.launch { wordTtsManager.speak(word) }
    }

    override fun finishPractice() {
        practiceQueue.immediateFinish()
    }

    private fun reportConfiguration(state: ScreenState.Configuring) {
        analyticsManager.sendEvent("letter_practice_configuration") {
            put("practice_type", configuration.practiceType.dataType.srsPracticeType.value)
            put("list_size", state.configuration.selectorState.selectedCountIntState.value)
        }
    }

    private fun LetterPracticeQueueState.Review.toScreenState(): ScreenState.Review {
        return ScreenState.Review(
            practiceProgress = progress,
            reviewState = getReviewStateUseCase(this)
        )
    }

    private fun LetterPracticeQueueState.Summary.toScreenState(): ScreenState.Summary {
        val accuracy: String? = items.filterIsInstance<LetterPracticeSummaryItem.Writing>()
            .takeIf { it.isNotEmpty() }
            ?.let {
                val totalStrokeCount = it.fold(0) { sum, item -> sum + item.strokeCount }
                val totalMistakeCount = it.fold(0) { sum, item -> sum + item.mistakes }
                val correctStrokes = (totalStrokeCount - totalMistakeCount)
                    .coerceAtLeast(0)
                (correctStrokes.toFloat() * 100f / totalStrokeCount).let { 
                    ((it * 100).roundToInt() / 100f).toString().removeSuffix(".0")
                }
            }
        return ScreenState.Summary(
            duration = duration,
            accuracy = accuracy,
            items = items
        )
    }

    private fun ScreenState.Review.kanaAutoReadFlow(): Flow<KanaReading> = callbackFlow {
        when {
            reviewState is LetterPracticeReviewState.Reading &&
                    reviewState.itemData is LetterPracticeItemData.KanaReadingData -> {

                snapshotFlow { reviewState.revealed.value }
                    .filter { it && reviewState.layout.kanaAutoPlay.value }
                    .take(1)
                    .onEach { send(reviewState.itemData.reading) }
                    .collect()

            }

            reviewState is LetterPracticeReviewState.Writing &&
                    reviewState.itemData is LetterPracticeItemData.KanaWritingData -> {

                writingCompletedFlow(reviewState)
                    .filter { reviewState.layout.kanaAutoPlay.value }
                    .onEach {
                        delay(200)
                        send(reviewState.itemData.reading)
                    }
                    .collect()

            }
        }
        awaitClose()
    }

    // Mirrors kanaAutoReadFlow above, but for the kanji writing quiz: speaks the kanji's most
    // common reading as soon as the drawing is verified (whether that's the study trace or the
    // from-memory review draw), reusing the same kanaAutoPlay preference toggle.
    private fun ScreenState.Review.kanjiAutoReadFlow(): Flow<String> = callbackFlow {
        if (reviewState is LetterPracticeReviewState.Writing &&
            reviewState.itemData is LetterPracticeItemData.KanjiWritingData
        ) {

            val word = reviewState.itemData.primaryReadingForSpeech

            if (word != null) {
                writingCompletedFlow(reviewState)
                    .filter { reviewState.layout.kanaAutoPlay.value }
                    .onEach {
                        delay(200)
                        send(word)
                    }
                    .collect()
            }

        }
        awaitClose()
    }

    // Emits once each time the active writer (study trace or from-memory review draw) finishes
    // and its strokes are verified. Re-subscribes whenever the writer instance itself changes
    // (e.g. study -> review), since each one completes independently.
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun writingCompletedFlow(reviewState: LetterPracticeReviewState.Writing): Flow<Unit> {
        return snapshotFlow { reviewState.writerState.value }
            .distinctUntilChanged()
            .flatMapLatest { writer ->
                snapshotFlow { writer.progress.value }
                    .filter { it is CharacterWritingProgress.Completed }
                    .take(1)
            }
            .map { }
    }

}
