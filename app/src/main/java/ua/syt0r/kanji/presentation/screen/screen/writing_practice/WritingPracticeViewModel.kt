package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ua.syt0r.kanji.core.curve_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawResult
import ua.syt0r.kanji_db_model.db.KanjiReadingTable
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

    override fun submitUserDrawnPath(drawData: DrawData): Flow<DrawResult> = flow {
        val currentState = state.value as State.ReviewingKanji

        val correctStroke = currentState.run {
            val index = min(strokes.size - 1, drawnStrokesCount)
            strokes[index]
        }

        val userDrawnStroke = drawData.drawnPath

        val isDrawnCorrectly = kanjiStrokeEvaluator.areSimilar(
            correctStroke,
            userDrawnStroke,
            drawData.drawAreaSizePx
        )

        if (isDrawnCorrectly) {

            state.value = currentState.run {
                copy(drawnStrokesCount = min(strokes.size, drawnStrokesCount + 1))
            }

        }

        emit(DrawResult())
    }

    private fun loadData(practiceId: Long) = flow<Unit> {
        val kanjiList = userDataRepository.getKanjiForPracticeSet(practiceId)
        val kanji = kanjiList.first()

        val strokes = kanjiRepository.getStrokes(kanji)
        val commands = strokes.map { SvgCommandParser.parse(it) }
        val paths = commands.map { SvgPathCreator.convert(it) }

        val readings = kanjiRepository.getReadings(kanji)

        state.postValue(
            State.ReviewingKanji(
                kanji = kanji,
                on = readings.filter { it.value == KanjiReadingTable.ReadingType.ON }
                    .keys
                    .toList(),
                kun = readings.filter { it.value == KanjiReadingTable.ReadingType.KUN }
                    .keys
                    .toList(),
                meanings = kanjiRepository.getMeanings(kanji),
                strokes = paths,
                drawnStrokesCount = 0
            )
        )

    }

}
