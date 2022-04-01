package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.stroke_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.use_case.LoadWritingPracticeDataUseCase
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.*
import java.util.*
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class WritingPracticeViewModel @Inject constructor(
    private val loadDataUseCase: LoadWritingPracticeDataUseCase,
    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator
) : ViewModel(), WritingPracticeScreenContract.ViewModel {

    companion object {
        private const val MINIMAL_LOADING_TIME = 600L
    }

    override val state = MutableLiveData<State>(State.Init)

    private val kanjiQueue: Queue<KanjiData> = LinkedList()
    private val kanjiStrokeQueue: Queue<Path> = LinkedList()

    private val mistakes = sortedMapOf<String, Int>()

    override fun init(practiceConfiguration: PracticeConfiguration) {
        if (state.value == State.Init) {
            state.value = State.Loading

            val loadingStartTime = System.currentTimeMillis()
            loadDataUseCase.load(practiceConfiguration)
                .onEach {
                    val timeToMinimalLoadingLeft =
                        MINIMAL_LOADING_TIME - System.currentTimeMillis() + loadingStartTime
                    val delayTime = max(0, min(MINIMAL_LOADING_TIME, timeToMinimalLoadingLeft))
                    delay(delayTime)
                }
                .flowOn(Dispatchers.IO)
                .onEach { kanjiDataList ->

                    kanjiQueue.addAll(kanjiDataList)

                    val kanjiData = kanjiQueue.peek()!!
                    kanjiStrokeQueue.addAll(kanjiData.strokes)

                    state.value = State.ReviewingKanji(
                        data = kanjiData,
                        progress = PracticeProgress(
                            totalItems = kanjiQueue.size,
                            currentItem = 1
                        )
                    )

                }
                .launchIn(viewModelScope)
        }
    }

    override fun submitUserDrawnPath(drawData: DrawData): Flow<DrawResult> = flow {
        val correctStroke = kanjiStrokeQueue.peek()!!
        val isDrawnCorrectly = kanjiStrokeEvaluator.areSimilar(correctStroke, drawData.drawnPath)

        if (isDrawnCorrectly) {
            emit(
                DrawResult.Correct(
                    userDrawnPath = drawData.drawnPath,
                    kanjiPath = correctStroke
                )
            )
        } else {
            val currentState = state.value as State.ReviewingKanji
            val path = if (currentState.mistakes >= 2) correctStroke else drawData.drawnPath
            emit(DrawResult.Mistake(path))
        }
    }

    override fun handleCorrectlyDrawnStroke() {
        kanjiStrokeQueue.poll()

        when {

            kanjiStrokeQueue.isEmpty() && kanjiQueue.size == 1 -> {
                kanjiQueue.poll()
                viewModelScope.launch {
                    delay(600)
                    state.value = State.Summary()
                }
            }

            kanjiStrokeQueue.isEmpty() -> {
                val drawnKanji = kanjiQueue.poll()
                val nextKanji = kanjiQueue.peek()!!

                kanjiStrokeQueue.addAll(nextKanji.strokes)
                val currentState = state.value as State.ReviewingKanji

                state.value = nextKanji.run {
                    State.ReviewingKanji(
                        data = nextKanji,
                        progress = currentState.progress.run { copy(currentItem = currentItem + 1) }
                    )
                }
            }

            else -> {
                val currentState = state.value as State.ReviewingKanji
                state.value = currentState.copy(
                    drawnStrokesCount = currentState.drawnStrokesCount + 1,
                    mistakes = 0
                )
            }

        }
    }

    override fun handleIncorrectlyDrawnStroke() {
        val currentState = state.value as State.ReviewingKanji
        val mistakesCount = currentState.mistakes + 1
        mistakes[currentState.data.kanji] = mistakes[currentState.data.kanji]?.plus(1) ?: 1
        state.value = currentState.copy(mistakes = mistakesCount)
    }

}
