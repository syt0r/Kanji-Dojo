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
import ua.syt0r.kanji.core.user_data.UserDataContract
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
    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator,
    private val repository: UserDataContract.PracticeRepository
) : ViewModel(), WritingPracticeScreenContract.ViewModel {

    private lateinit var practiceConfiguration: WritingPracticeConfiguration

    private val reviewCharacterQueue: Queue<ReviewCharacterData> = LinkedList()
    private val kanjiStrokeQueue: Queue<Path> = LinkedList()

    private val kanjiMistakes = mutableMapOf<String, Int>()
    private val kanjiReviewData = mutableListOf<KanjiWritingReview>()

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun init(practiceConfiguration: WritingPracticeConfiguration) {
        if (!this::practiceConfiguration.isInitialized) {
            this.practiceConfiguration = practiceConfiguration

            viewModelScope.launch {
                val kanjiDataList = withContext(Dispatchers.IO) {
                    loadDataUseCase.load(practiceConfiguration)
                }
                reviewCharacterQueue.addAll(kanjiDataList)
                loadCurrentKanji()
            }
        }
    }

    override suspend fun submitUserDrawnPath(drawData: DrawData): DrawResult {
        val correctStroke = kanjiStrokeQueue.peek()!!

        val isDrawnCorrectly = withContext(Dispatchers.IO) {
            kanjiStrokeEvaluator.areStrokesSimilar(correctStroke, drawData.drawnPath)
        }

        return if (isDrawnCorrectly) {
            DrawResult.Correct(
                userDrawnPath = drawData.drawnPath,
                kanjiPath = correctStroke
            )
        } else {
            val currentState = state.value as ScreenState.Review
            val path = if (currentState.mistakes >= 2) correctStroke else drawData.drawnPath
            DrawResult.Mistake(path)
        }
    }

    override fun handleCorrectlyDrawnStroke() {
        kanjiStrokeQueue.poll()

        val currentState = state.value as ScreenState.Review

        when {

            kanjiStrokeQueue.isEmpty() && reviewCharacterQueue.size == 1 -> {
                addKanjiReview(currentState)
                reviewCharacterQueue.poll()
                loadSummary()
            }

            kanjiStrokeQueue.isEmpty() -> {
                addKanjiReview(currentState)
                reviewCharacterQueue.poll()
                loadCurrentKanji()
            }

            else -> {
                state.value = currentState.copy(
                    drawnStrokesCount = currentState.drawnStrokesCount + 1,
                    mistakes = 0
                )
            }

        }
    }

    override fun handleIncorrectlyDrawnStroke() {
        val currentState = state.value as ScreenState.Review
        val mistakesCount = currentState.mistakes + 1
        kanjiMistakes[currentState.data.character] = kanjiMistakes[currentState.data.character]
            ?.plus(1) ?: 1
        state.value = currentState.copy(mistakes = mistakesCount)
    }

    private fun loadCurrentKanji() {
        val kanjiData = reviewCharacterQueue.peek()!!
        kanjiStrokeQueue.addAll(kanjiData.strokes)

        val totalKanjiInReview = practiceConfiguration.characterList.size

        state.value = ScreenState.Review(
            data = kanjiData,
            progress = PracticeProgress(
                totalItems = totalKanjiInReview,
                currentItem = totalKanjiInReview - reviewCharacterQueue.size + 1
            )
        )
    }

    private fun addKanjiReview(state: ScreenState.Review) {
        val review = state.run {
            KanjiWritingReview(
                kanji = data.character,
                practiceSetId = practiceConfiguration.practiceId,
                reviewTime = LocalDateTime.now(),
                mistakes = kanjiMistakes[data.character] ?: 0
            )
        }
        kanjiReviewData.add(review)
    }

    private fun loadSummary() {
        viewModelScope.launch {
            state.value = ScreenState.Summary(kanjiReviewData)
            withContext(Dispatchers.IO) {
                repository.saveReview(kanjiReviewData)
            }
        }
    }

}
