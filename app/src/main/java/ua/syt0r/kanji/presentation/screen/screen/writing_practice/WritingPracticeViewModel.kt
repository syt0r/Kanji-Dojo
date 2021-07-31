package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.syt0r.kanji.core.curve_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.svg.SvgCommandParser
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class WritingPracticeViewModel @Inject constructor(
    private val kanjiRepository: KanjiDataContract.Repository
) : ViewModel(), WritingPracticeScreenContract.ViewModel {

    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator = KanjiStrokeEvaluator()

    override val state = MutableLiveData<WritingPracticeScreenContract.State>()

    override fun init(kanji: String) {
        if (state.value == null) {

            val strokes = kanjiRepository.getStrokes(kanji)
            val commands = strokes.map { SvgCommandParser.parse(it) }
            val paths = commands.map { SvgPathCreator.convert(it) }

            state.value = WritingPracticeScreenContract.State.DrawingKanji(
                stokes = paths,
                drawnStrokesCount = 0
            )

        }
    }

    override fun submitUserDrawnPath(path: Path, areaSize: Int) {

        val currentState = state.value as WritingPracticeScreenContract.State.DrawingKanji
        val predefinedPath = currentState.run {
            val index = min(stokes.size - 1, drawnStrokesCount)
            stokes[index]
        }

        if (kanjiStrokeEvaluator.areSimilar(predefinedPath, path, areaSize)) {

            state.value = currentState.run {
                copy(drawnStrokesCount = min(stokes.size, drawnStrokesCount + 1))
            }

        }

    }

}