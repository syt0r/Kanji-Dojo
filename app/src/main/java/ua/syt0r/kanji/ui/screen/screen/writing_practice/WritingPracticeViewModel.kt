package ua.syt0r.kanji.ui.screen.screen.writing_practice

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.syt0r.kanji.core.curve_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStoreContract
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.svg_parser.SvgCommandParser
import kotlin.math.min

class WritingPracticeViewModel(
    private val kanjiDataStore: KanjiDataStoreContract.DataStore
) : ViewModel(), WritingPracticeScreenContract.ViewModel {

    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator = KanjiStrokeEvaluator()

    override val state = MutableLiveData<WritingPracticeScreenContract.State>()

    override fun init(kanji: String) {
        if (state.value == null) {

            val strokes = kanjiDataStore.getStrokes(kanji)
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