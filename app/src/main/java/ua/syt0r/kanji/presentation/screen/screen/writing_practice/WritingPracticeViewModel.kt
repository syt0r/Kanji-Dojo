package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.analytics.AnalyticsContract
import ua.syt0r.kanji.core.stroke_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.*
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.use_case.LoadWritingPracticeDataUseCase
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WritingPracticeViewModel @Inject constructor(
    private val loadDataUseCase: LoadWritingPracticeDataUseCase,
    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator,
    private val repository: UserDataContract.PracticeRepository,
    private val analyticsManager: AnalyticsContract.Manager
) : ViewModel(), WritingPracticeScreenContract.ViewModel {

    private lateinit var practiceConfiguration: WritingPracticeConfiguration

    private val characterQueue: Queue<ReviewCharacterData> = LinkedList()
    private val strokesQueue: Queue<Path> = LinkedList()

    private val characterMistakesMap = mutableMapOf<String, Int>()
    private val characterReviewList = mutableListOf<CharacterReviewResult>()

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun init(practiceConfiguration: WritingPracticeConfiguration) {
        if (!this::practiceConfiguration.isInitialized) {
            this.practiceConfiguration = practiceConfiguration

            viewModelScope.launch {
                val kanjiDataList = withContext(Dispatchers.IO) {
                    loadDataUseCase.load(practiceConfiguration)
                }
                characterQueue.addAll(kanjiDataList)
                loadCurrentKanji()
            }
        }
    }

    override suspend fun submitUserDrawnPath(drawData: DrawData): DrawResult {
        val correctStroke = strokesQueue.peek() ?: return DrawResult.IgnoreCompletedPractice

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
        strokesQueue.poll()

        val currentState = state.value as ScreenState.Review

        when {

            strokesQueue.isEmpty() && characterQueue.size == 1 -> {
                addCharacterReviewResult(currentState)
                characterQueue.poll()
                loadSummary()
            }

            strokesQueue.isEmpty() -> {
                addCharacterReviewResult(currentState)
                characterQueue.poll()
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
        characterMistakesMap[currentState.data.character] =
            characterMistakesMap[currentState.data.character]
                ?.plus(1) ?: 1
        state.value = currentState.copy(mistakes = mistakesCount)
    }

    private fun loadCurrentKanji() {
        val kanjiData = characterQueue.peek()!!
        strokesQueue.addAll(kanjiData.strokes)

        val totalKanjiInReview = practiceConfiguration.characterList.size

        state.value = ScreenState.Review(
            data = kanjiData,
            practiceMode = practiceConfiguration.practiceMode,
            progress = PracticeProgress(
                totalItems = totalKanjiInReview,
                currentItem = totalKanjiInReview - characterQueue.size + 1
            )
        )
    }

    private fun addCharacterReviewResult(state: ScreenState.Review) {
        val review = state.run {
            CharacterReviewResult(
                character = data.character,
                practiceSetId = practiceConfiguration.practiceId,
                reviewTime = LocalDateTime.now(),
                mistakes = characterMistakesMap[data.character] ?: 0
            )
        }
        characterReviewList.add(review)
    }

    private fun loadSummary() {
        viewModelScope.launch {
            state.value = ScreenState.Summary.Saving
            val data = withContext(Dispatchers.IO) {

                if (practiceConfiguration.practiceMode == WritingPracticeMode.Review) {
                    repository.saveReview(characterReviewList)
                }

                characterReviewList.map {
                    ReviewResult(
                        characterReviewResult = it,
                        reviewScore = if (it.mistakes > 2) ReviewScore.Bad else ReviewScore.Good
                    )
                }.also { reportReviewResult(it) }

            }
            state.value = ScreenState.Summary.Saved(data)
        }
    }

    private fun reportReviewResult(results: List<ReviewResult>) {
        analyticsManager.sendEvent("writing_practice_summary") {
            putInt("practice_size", results.size)
            putInt("total_mistakes", results.sumOf { it.characterReviewResult.mistakes })
            putLong(
                "review_duration_sec",
                ChronoUnit.SECONDS.between(
                    results.minOf { it.characterReviewResult.reviewTime },
                    results.maxOf { it.characterReviewResult.reviewTime }
                )
            )
        }
        results.forEach {
            analyticsManager.sendEvent("char_reviewed") {
                putString("char", it.characterReviewResult.character)
                putInt("mistakes", it.characterReviewResult.mistakes)
            }
        }
    }

}
