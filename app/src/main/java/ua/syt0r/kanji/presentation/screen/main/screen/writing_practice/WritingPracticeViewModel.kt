package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

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
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.*
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState
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

    sealed class ReviewAction {
        object Study : ReviewAction()
        object Review : ReviewAction()
    }

    private data class ReviewQueueItem(
        val characterData: ReviewCharacterData,
        val history: List<ReviewAction>
    )

    companion object {
        private const val RepeatIndexShift = 2
    }

    private lateinit var practiceConfiguration: WritingPracticeConfiguration

    private val reviewItemsQueue = LinkedList<ReviewQueueItem>()
    private val strokesQueue: Queue<Path> = LinkedList()

    private val mistakesMap = mutableMapOf<String, Int>()

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    private lateinit var practiceStartTime: LocalDateTime

    override fun init(practiceConfiguration: WritingPracticeConfiguration) {
        if (!this::practiceConfiguration.isInitialized) {
            this.practiceConfiguration = practiceConfiguration
            practiceStartTime = LocalDateTime.now()

            viewModelScope.launch {

                val action = if (practiceConfiguration.isStudyMode) ReviewAction.Study
                else ReviewAction.Review

                val reviewItems = withContext(Dispatchers.IO) {
                    loadDataUseCase.load(practiceConfiguration)
                        .map {
                            ReviewQueueItem(
                                characterData = it,
                                history = listOf(action)
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

    override fun loadNextCharacter(userAction: ReviewUserAction) {
        val currentState = state.value as ScreenState.Review
        val reviewItem = reviewItemsQueue.peek()!!
        reviewItemsQueue.poll()

        when (userAction) {
            ReviewUserAction.StudyNext -> {
                val newItem = reviewItem.copy(
                    history = reviewItem.history.plus(ReviewAction.Review)
                )
                reviewItemsQueue.add(0, newItem)
            }
            ReviewUserAction.Repeat -> {
                val newReviewItem = ReviewQueueItem(
                    characterData = currentState.data,
                    history = reviewItem.history.plus(ReviewAction.Review)
                )
                val insertPosition = Integer.min(RepeatIndexShift, reviewItemsQueue.size)
                Logger.d("insertPosition[$insertPosition]")
                reviewItemsQueue.add(insertPosition, newReviewItem)
            }
            ReviewUserAction.Next -> {}
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

        val pendingCount = if (practiceConfiguration.isStudyMode) {
            reviewItemsQueue.count { it.history.size <= 2 }
        } else {
            reviewItemsQueue.count { it.history.size == 1 }
        }

        val repeatCount = reviewItemsQueue.size - pendingCount

        val progress = PracticeProgress(
            pendingCount = pendingCount,
            repeatCount = repeatCount,
            finishedCount = initialKanjiCount - pendingCount - repeatCount
        )

        state.value = ScreenState.Review(
            data = reviewItem.characterData,
            isStudyMode = reviewItem.history.last() == ReviewAction.Study,
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
