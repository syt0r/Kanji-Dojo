package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.stroke_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.*
import java.lang.Integer.min
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WritingPracticeViewModel @Inject constructor(
    private val loadDataUseCase: WritingPracticeScreenContract.LoadWritingPracticeDataUseCase,
    private val isEligibleForInAppReviewUseCase: WritingPracticeScreenContract.IsEligibleForInAppReviewUseCase,
    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator,
    private val repository: UserDataContract.PracticeRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel(), WritingPracticeScreenContract.ViewModel {

    private enum class Reason { Study, Review, Repeat }

    private data class ReviewItem(
        val characterData: ReviewCharacterData,
        val reason: Reason
    )

    companion object {
        private const val CharacterMistakesToRepeat = 3
        private const val RepeatIndexShift = 2
    }

    private lateinit var practiceConfiguration: WritingPracticeConfiguration

    private val reviewItemsQueue = LinkedList<ReviewItem>()
    private val strokesQueue: Queue<Path> = LinkedList()

    private val mistakesMap = mutableMapOf<String, Int>()

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    private lateinit var practiceStartTime: LocalDateTime

    override fun init(practiceConfiguration: WritingPracticeConfiguration) {
        if (!this::practiceConfiguration.isInitialized) {
            this.practiceConfiguration = practiceConfiguration
            practiceStartTime = LocalDateTime.now()

            viewModelScope.launch {
                val reviewItems = withContext(Dispatchers.IO) {
                    loadDataUseCase.load(practiceConfiguration)
                        .map {
                            ReviewItem(
                                characterData = it,
                                reason = if (practiceConfiguration.isStudyMode) Reason.Study
                                else Reason.Review
                            )
                        }
                }
                reviewItemsQueue.addAll(reviewItems)
                updateReviewState()
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
            val path = correctStroke.takeIf { currentState.currentStrokeMistakes >= 2 }
                ?: drawData.drawnPath
            DrawResult.Mistake(path)
        }
    }

    override fun handleCorrectlyDrawnStroke() {
        val currentState = state.value as ScreenState.Review

        if (!currentState.isStudyMode) {
            mistakesMap[currentState.data.character] = mistakesMap[currentState.data.character]
                ?.plus(currentState.currentStrokeMistakes)
                ?: currentState.currentStrokeMistakes
        }

        strokesQueue.poll()

        state.value = currentState.copy(
            drawnStrokesCount = currentState.drawnStrokesCount + 1,
            currentStrokeMistakes = 0
        )
    }

    override fun handleIncorrectlyDrawnStroke() {
        val currentState = state.value as ScreenState.Review
        state.value = currentState.run {
            copy(
                currentStrokeMistakes = currentStrokeMistakes + 1,
                currentCharacterMistakes = currentCharacterMistakes + 1
            )
        }
    }

    override fun loadNextCharacter() {
        val currentState = state.value as ScreenState.Review
        reviewItemsQueue.poll()

        when {
            currentState.isStudyMode -> {
                reviewItemsQueue.add(0, ReviewItem(currentState.data, Reason.Review))
            }
            currentState.currentCharacterMistakes >= CharacterMistakesToRepeat -> {
                val reviewItem = ReviewItem(currentState.data, Reason.Repeat)
                val insertPosition = min(RepeatIndexShift, reviewItemsQueue.size)
                Logger.d("insertPosition[$insertPosition]")
                reviewItemsQueue.add(insertPosition, reviewItem)
            }
        }

        if (reviewItemsQueue.isEmpty()) {
            loadSummary()
        } else {
            updateReviewState()
        }
    }

    private fun updateReviewState() {
        val reviewItem = reviewItemsQueue.peek()!!
        strokesQueue.addAll(reviewItem.characterData.strokes)

        val initialKanjiCount = practiceConfiguration.characterList.size
        val pendingCount = reviewItemsQueue
            .distinctBy { it.characterData.character }
            .count { it.reason != Reason.Repeat }
        val repeatCount = reviewItemsQueue.count { it.reason == Reason.Repeat }
        val progress = PracticeProgress(
            pendingCount = pendingCount,
            repeatCount = repeatCount,
            finishedCount = initialKanjiCount - pendingCount - repeatCount
        )

        state.value = ScreenState.Review(
            data = reviewItem.characterData,
            isStudyMode = reviewItem.reason == Reason.Study,
            progress = progress
        )
    }

    private fun loadSummary() {
        viewModelScope.launch {
            state.value = ScreenState.Summary.Saving
            withContext(Dispatchers.IO) {

                val characterReviewList = mistakesMap.map { (character, mistakes) ->
                    CharacterReviewResult(character, practiceConfiguration.practiceId, mistakes)
                }

                repository.saveReview(
                    time = practiceStartTime,
                    reviewResultList = characterReviewList,
                    isStudyMode = practiceConfiguration.isStudyMode
                )

                val reviewResultList = characterReviewList.map {
                    ReviewResult(
                        characterReviewResult = it,
                        reviewScore = if (it.mistakes > 2) ReviewScore.Bad else ReviewScore.Good
                    )
                }.also { reportReviewResult(it) }


                state.value = ScreenState.Summary.Saved(
                    reviewResultList = reviewResultList,
                    eligibleForInAppReview = isEligibleForInAppReviewUseCase.check()
                )
            }
        }
    }

    private fun reportReviewResult(results: List<ReviewResult>) {
        analyticsManager.sendEvent("writing_practice_summary") {
            putInt("practice_size", results.size)
            putInt("total_mistakes", results.sumOf { it.characterReviewResult.mistakes })
            putLong(
                "review_duration_sec",
                ChronoUnit.SECONDS.between(practiceStartTime, LocalDateTime.now())
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
