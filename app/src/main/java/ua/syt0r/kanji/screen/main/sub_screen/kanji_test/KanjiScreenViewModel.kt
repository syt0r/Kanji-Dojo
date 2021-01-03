package ua.syt0r.kanji.screen.main.sub_screen.kanji_test

import android.app.Application
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.kanji.screen.main.DataSource
import ua.syt0r.svg_parser.SvgCommandParser
import kotlin.math.min

class KanjiScreenViewModel(
    application: Application
) : AndroidViewModel(application), KanjiScreenContract.ViewModel {

    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator = KanjiStrokeEvaluator()

    override val state = MutableLiveData<KanjiScreenContract.State>()

    override fun init(kanjiIndex: Int) {
        if (state.value == null) {
            val kanji = DataSource.data[kanjiIndex]
            state.value = KanjiScreenContract.State.DrawingKanji(
                stokes = kanji.strokes
                    .map { SvgCommandParser.parse(it) }
                    .map { SvgPathCreator.convert(it) },
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