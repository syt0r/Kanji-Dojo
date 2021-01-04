package ua.syt0r.kanji.screen.main.sub_screen.review

import android.app.Application
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ua.syt0r.kanji.core.curve_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStoreContract
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.svg_parser.SvgCommandParser
import kotlin.math.min

class KanjiScreenViewModel(
    application: Application,
    private val kanjiDataStore: KanjiDataStoreContract.DataStore
) : AndroidViewModel(application), KanjiScreenContract.ViewModel {

    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator = KanjiStrokeEvaluator()

    override val state = MutableLiveData<KanjiScreenContract.State>()

    override fun init(kanji: String) {
        if (state.value == null) {

            val strokes = kanjiDataStore.getStrokes(kanji)
            val commands = strokes.map { SvgCommandParser.parse(it) }
            val paths = commands.map { SvgPathCreator.convert(it) }

            state.value = KanjiScreenContract.State.DrawingKanji(
                stokes = paths,
                drawnStrokesCount = 0
            )

        }
    }

    override fun submitUserDrawnPath(path: Path, areaSize: Int) {

        val currentState = state.value as KanjiScreenContract.State.DrawingKanji
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