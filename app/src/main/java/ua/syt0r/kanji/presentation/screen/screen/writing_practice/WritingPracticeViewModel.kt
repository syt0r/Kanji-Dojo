package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.stroke_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.user_data.model.KanjiWritingReview
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.*
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.use_case.LoadWritingPracticeDataUseCase
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WritingPracticeViewModel @Inject constructor(
    private val loadDataUseCase: LoadWritingPracticeDataUseCase,
    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator
) : ViewModel(), WritingPracticeScreenContract.ViewModel {

    private lateinit var practiceConfiguration: PracticeConfiguration

    private val kanjiQueue: Queue<KanjiData> = LinkedList()
    private val kanjiStrokeQueue: Queue<Path> = LinkedList()

    private val kanjiReviewData = mutableListOf<KanjiWritingReview>()

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun init(practiceConfiguration: PracticeConfiguration) {
        if (!this::practiceConfiguration.isInitialized) {
            this.practiceConfiguration = practiceConfiguration

            viewModelScope.launch {
                val kanjiDataList = withContext(Dispatchers.IO) {
                    loadDataUseCase.load(practiceConfiguration)
                }
                kanjiQueue.addAll(kanjiDataList)
                loadCurrentKanji()
            }
        }
    }

    override suspend fun submitUserDrawnPath(drawData: DrawData): DrawResult {
        val correctStroke = kanjiStrokeQueue.peek()!!

        val isDrawnCorrectly = withContext(Dispatchers.IO) {
            kanjiStrokeEvaluator.areSimilar(correctStroke, drawData.drawnPath)
        }

        return if (isDrawnCorrectly) {
            DrawResult.Correct(
                userDrawnPath = drawData.drawnPath,
                kanjiPath = correctStroke
            )
        } else {
            val currentState = state.value as ScreenState.ReviewingKanji
            val path = if (currentState.mistakes >= 2) correctStroke else drawData.drawnPath
            DrawResult.Mistake(path)
        }
    }

    override fun handleCorrectlyDrawnStroke() {
        kanjiStrokeQueue.poll()

        when {

            kanjiStrokeQueue.isEmpty() && kanjiQueue.size == 1 -> {
                kanjiQueue.poll()
                state.value = ScreenState.Summary()
            }

            kanjiStrokeQueue.isEmpty() -> {
                kanjiQueue.poll()
                val currentState = state.value as ScreenState.ReviewingKanji
                addKanjiReview(currentState)
                loadCurrentKanji()
            }

            else -> {
                val currentState = state.value as ScreenState.ReviewingKanji
                state.value = currentState.copy(
                    drawnStrokesCount = currentState.drawnStrokesCount + 1,
                    mistakes = 0
                )
            }

        }
    }

    override fun handleIncorrectlyDrawnStroke() {
        val currentState = state.value as ScreenState.ReviewingKanji
        val mistakesCount = currentState.mistakes + 1
        state.value = currentState.copy(mistakes = mistakesCount)
    }

    private fun loadCurrentKanji() {
        val kanjiData = kanjiQueue.peek()!!
        kanjiStrokeQueue.addAll(kanjiData.strokes)

        val totalKanjiInReview = practiceConfiguration.kanjiList.size

        state.value = ScreenState.ReviewingKanji(
            data = kanjiData,
            progress = PracticeProgress(
                totalItems = totalKanjiInReview,
                currentItem = totalKanjiInReview - kanjiQueue.size + 1
            )
        )
    }

    private fun addKanjiReview(state: ScreenState.ReviewingKanji) {
        val review = state.run {
            KanjiWritingReview(
                kanji = data.kanji,
                practiceSetId = practiceConfiguration.practiceId,
                reviewTime = LocalDateTime.now(),
                mistakes = state.mistakes
            )
        }
        kanjiReviewData.add(review)
    }

}
