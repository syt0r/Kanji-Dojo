package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import ua.syt0r.kanji.core.curve_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.State
import ua.syt0r.svg.SvgCommandParser
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class WritingPracticeViewModel @Inject constructor(
    private val userDataRepository: UserDataContract.WritingRepository,
    private val kanjiRepository: KanjiDataContract.Repository
) : ViewModel(), WritingPracticeScreenContract.ViewModel {

    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator = KanjiStrokeEvaluator()

    override val state = MutableLiveData<State>(State.Init)

    override fun init(practiceId: Long) {
        if (state.value == State.Init) {
            loadData(practiceId).flowOn(Dispatchers.IO).launchIn(viewModelScope)
        }
    }

    override fun submitUserDrawnPath(path: Path, areaSize: Int) {

        val currentState = state.value as State.ReviewingKanji
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

    private fun loadData(practiceId: Long) = flow<Unit> {
        val kanjiList = userDataRepository.getKanjiForPracticeSet(practiceId)
        val kanji = kanjiList.first()

        val strokes = kanjiRepository.getStrokes(kanji)
        val commands = strokes.map { SvgCommandParser.parse(it) }
        val paths = commands.map { SvgPathCreator.convert(it) }

        state.postValue(
            State.ReviewingKanji(
                kanji = kanji,
                on = listOf("た。いる"),
                kun = listOf("ゴン", "ゲン"),
                meanings = kanjiRepository.getMeanings(kanji),
                stokes = paths,
                drawnStrokesCount = 0
            )
        )

    }

}
