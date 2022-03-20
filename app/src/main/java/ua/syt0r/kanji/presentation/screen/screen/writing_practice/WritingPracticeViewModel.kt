package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.stroke_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.use_case.LoadWritingPracticeDataUseCase
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawResult
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.KanjiData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeConfiguration
import java.util.*
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class WritingPracticeViewModel @Inject constructor(
    private val loadDataUseCase: LoadWritingPracticeDataUseCase,
    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator
) : ViewModel(), WritingPracticeScreenContract.ViewModel {

    override val state = MutableLiveData<State>(State.Init)

    private val pendingKanjiList: Queue<KanjiData> = LinkedList()
    private val pendingKanjiStrokes: Queue<Path> = LinkedList()

    private val reviewedKanjiMistakesMap: MutableMap<KanjiData, Int> = mutableMapOf()

    override fun init(practiceConfiguration: PracticeConfiguration) {
        if (state.value == State.Init) {
            state.value = State.Loading

            loadDataUseCase.load(practiceConfiguration)
                .flowOn(Dispatchers.IO)
                .onEach { kanjiDataList ->

                    pendingKanjiList.addAll(kanjiDataList)

                    val kanjiData = pendingKanjiList.peek()!!
                    pendingKanjiStrokes.addAll(kanjiData.strokes)

                    kanjiData.apply {
                        state.value = State.ReviewingKanji(
                            kanji = kanji,
                            on = on,
                            kun = kun,
                            meanings = meanings,
                            strokes = strokes
                        )
                    }

                }
                .launchIn(viewModelScope)
        }
    }

    override fun submitUserDrawnPath(drawData: DrawData): Flow<DrawResult> = flow {
        Logger.d("submitUserDrawnPath")
        val correctStroke = pendingKanjiStrokes.peek() ?: return@flow
        val isDrawnCorrectly = kanjiStrokeEvaluator.areSimilar(correctStroke, drawData.drawnPath)
        val currentState = state.value as State.ReviewingKanji

        if (isDrawnCorrectly) {
            state.value = currentState.run {
                copy(drawnStrokesCount = min(drawnStrokesCount + 1, strokes.size))
            }
            emit(
                DrawResult.Correct(
                    userDrawnPath = drawData.drawnPath,
                    kanjiPath = correctStroke
                )
            )
            pendingKanjiStrokes.poll()
        } else {
            val path = if (currentState.mistakes > 2) correctStroke else drawData.drawnPath
            state.value = currentState.run {
                copy(mistakes = mistakes + 1)
            }
            emit(
                DrawResult.Mistake(
                    path = path
                )
            )
        }
    }

    override fun handleCorrectlyDrawnStroke() {
        val currentState = state.value as State.ReviewingKanji

        if (pendingKanjiStrokes.isEmpty()) {

            val drawnKanji = pendingKanjiList.poll()!!
            reviewedKanjiMistakesMap[drawnKanji] = currentState.mistakes

            if (pendingKanjiList.isEmpty()) {

                state.value = State.Summary("")

            } else {

                val kanjiData = pendingKanjiList.peek()!!
                pendingKanjiStrokes.addAll(kanjiData.strokes)

                state.value = kanjiData.run {
                    State.ReviewingKanji(
                        kanji = kanji,
                        on = on,
                        kun = kun,
                        meanings = meanings,
                        strokes = strokes
                    )
                }

            }
        }
    }

    override fun handleIncorrectlyDrawnStroke() {
        val currentState = state.value as State.ReviewingKanji
        val mistakesCount = currentState.mistakes + 1
        state.value = currentState.copy(mistakes = mistakesCount)
    }

}
