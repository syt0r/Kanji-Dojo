package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Path
import kotlinx.coroutines.*
import kotlinx.datetime.Instant
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.stroke_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.core.user_data.model.CharacterReviewOutcome
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.core.user_data.model.OutcomeSelectionConfiguration
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.*
import java.util.*
import kotlin.math.max
import kotlin.time.Duration

class WritingPracticeViewModel(
    private val viewModelScope: CoroutineScope,
    private val loadDataUseCase: WritingPracticeScreenContract.LoadWritingPracticeDataUseCase,
    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator,
    private val practiceRepository: PracticeRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val analyticsManager: AnalyticsManager,
    private val timeUtils: TimeUtils
) : WritingPracticeScreenContract.ViewModel {

    sealed class ReviewAction {
        object Study : ReviewAction()
        object Review : ReviewAction()
    }

    private data class ReviewQueueItem(
        val character: String,
        val characterData: Deferred<ReviewCharacterData>,
        val history: List<ReviewAction>
    )

    companion object {
        private const val RepeatIndexShift = 2
    }

    private lateinit var practiceConfiguration: MainDestination.Practice.Writing
    private lateinit var screenConfiguration: WritingScreenConfiguration

    private val reviewItemsQueue = LinkedList<ReviewQueueItem>()
    private val strokesQueue: Queue<Path> = LinkedList()

    private val mistakesMap = mutableMapOf<String, Int>()
    private val strokesCount = mutableMapOf<String, Int>()
    private val reviewTimeMap = mutableMapOf<String, Duration>()

    private lateinit var practiceStartTime: Instant
    private lateinit var currentReviewStartTime: Instant

    private var totalReviewsCount = 0

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun init(practiceConfiguration: MainDestination.Practice.Writing) {
        if (!this::practiceConfiguration.isInitialized) {
            this.practiceConfiguration = practiceConfiguration
            practiceStartTime = timeUtils.now()

            viewModelScope.launch {

                screenConfiguration = WritingScreenConfiguration(
                    shouldHighlightRadicals = mutableStateOf(
                        preferencesRepository.getShouldHighlightRadicals()
                    ),
                    noTranslationsLayout = preferencesRepository.getNoTranslationsLayoutEnabled(),
                    leftHandedMode = preferencesRepository.getLeftHandedModeEnabled()
                )

                val initialAction = if (practiceConfiguration.isStudyMode) {
                    ReviewAction.Study
                } else {
                    ReviewAction.Review
                }

                val queueItems = practiceConfiguration.characterList.map { character ->
                    ReviewQueueItem(
                        character = character,
                        characterData = async(
                            context = Dispatchers.IO,
                            start = CoroutineStart.LAZY
                        ) {
                            loadDataUseCase.load(character)
                        },
                        history = listOf(initialAction)
                    )
                }

                reviewItemsQueue.addAll(queueItems)
                loadCurrentReview()
            }
        }
    }

    override suspend fun submitUserDrawnPath(inputData: StrokeInputData): StrokeProcessingResult {
        Logger.d(">>")
        val correctStroke = strokesQueue.peek()!!

        val isDrawnCorrectly = withContext(Dispatchers.IO) {
            kanjiStrokeEvaluator.areStrokesSimilar(correctStroke, inputData.path)
        }

        val result = if (isDrawnCorrectly) {
            handleCorrectlyDrawnStroke()
            StrokeProcessingResult.Correct(
                userPath = inputData.path,
                kanjiPath = correctStroke
            )
        } else {
            handleIncorrectlyDrawnStroke()
            val currentStrokeMistakes = state.value.let { it as ScreenState.Review }
                .reviewState.value
                .currentStrokeMistakes
            val path = if (currentStrokeMistakes > 2) correctStroke else inputData.path
            StrokeProcessingResult.Mistake(path)
        }
        Logger.d("<< result[$result]")
        return result
    }

    override fun savePractice(
        outcomeSelectionConfiguration: OutcomeSelectionConfiguration,
        outcomes: Map<String, CharacterReviewOutcome>
    ) {
        state.value = ScreenState.Loading
        viewModelScope.launch {
            state.value = withContext(Dispatchers.IO) {
                preferencesRepository.setWritingOutcomeSelectionConfiguration(
                    config = outcomeSelectionConfiguration
                )
                val characterReviewList = mistakesMap.map { (character, mistakes) ->
                    CharacterReviewResult(
                        character = character,
                        practiceId = practiceConfiguration.practiceId,
                        mistakes = mistakes,
                        reviewDuration = reviewTimeMap.getValue(character),
                        outcome = if (mistakes > 2) CharacterReviewOutcome.Fail else CharacterReviewOutcome.Success
                    )
                }

                practiceRepository.saveWritingReview(
                    practiceTime = practiceStartTime,
                    reviewResultList = characterReviewList,
                    isStudyMode = practiceConfiguration.isStudyMode
                )

                val totalStrokes = strokesCount.values.sum()
                val totalMistakes = characterReviewList.sumOf { it.mistakes }

                ScreenState.Saved(
                    practiceDuration = reviewTimeMap.values
                        .reduce { acc, duration -> acc.plus(duration) },
                    accuracy = max(totalStrokes - totalMistakes, 0) / totalStrokes.toFloat() * 100,
                    repeatCharacters = outcomes.filter { it.value == CharacterReviewOutcome.Fail }.keys.toList(),
                    goodCharacters = outcomes.filter { it.value == CharacterReviewOutcome.Success }.keys.toList()
                )
            }
        }
    }

    override fun onHintClick() {
        handleIncorrectlyDrawnStroke()
    }

    private fun handleCorrectlyDrawnStroke() {
        Logger.d(">>")
        val screenState = state.value as ScreenState.Review
        val reviewState = screenState.reviewState as MutableState
        val reviewData = reviewState.value

        if (!reviewData.isStudyMode) {
            mistakesMap[reviewData.characterData.character] =
                mistakesMap[reviewData.characterData.character]
                    ?.plus(reviewData.currentStrokeMistakes)
                    ?: reviewData.currentStrokeMistakes
        }

        strokesQueue.poll()

        reviewState.value = reviewData.copy(
            drawnStrokesCount = reviewData.drawnStrokesCount + 1,
            currentStrokeMistakes = 0
        )
        Logger.d("<<")
    }

    private fun handleIncorrectlyDrawnStroke() {
        val screenState = state.value as ScreenState.Review
        val reviewState = screenState.reviewState as MutableState
        val reviewData = reviewState.value
        reviewState.value = reviewData.run {
            copy(
                currentStrokeMistakes = currentStrokeMistakes + 1,
                currentCharacterMistakes = currentCharacterMistakes + 1
            )
        }
    }

    override fun loadNextCharacter(userAction: ReviewUserAction) {
        val queueItem = reviewItemsQueue.poll()

        val currentCharacter = queueItem.character
        val reviewDuration = timeUtils.now() - currentReviewStartTime
        reviewTimeMap[currentCharacter] = reviewTimeMap[currentCharacter]
            ?.plus(reviewDuration)
            ?: reviewDuration

        when (userAction) {
            ReviewUserAction.StudyNext -> {
                val newItem = queueItem.copy(
                    history = queueItem.history.plus(ReviewAction.Review)
                )
                reviewItemsQueue.add(0, newItem)
            }

            ReviewUserAction.Repeat -> {
                val newReviewItem = queueItem.copy(
                    history = queueItem.history.plus(ReviewAction.Review)
                )
                val insertPosition = Integer.min(RepeatIndexShift, reviewItemsQueue.size)
                reviewItemsQueue.add(insertPosition, newReviewItem)
            }

            ReviewUserAction.Next -> {}
        }

        if (reviewItemsQueue.isEmpty()) {
            loadSummary()
        } else {
            loadCurrentReview()
        }
    }

    override fun toggleRadicalsHighlight() {
        val mutableState = screenConfiguration.shouldHighlightRadicals as MutableState
        val updatedValue = mutableState.value.not()
        mutableState.value = updatedValue
        viewModelScope.launch {
            preferencesRepository.setShouldHighlightRadicals(updatedValue)
        }
    }

    override fun reportScreenShown(configuration: MainDestination.Practice.Writing) {
        analyticsManager.setScreen("writing_practice")
        analyticsManager.sendEvent("writing_practice_configuration") {
            put("list_size", configuration.characterList.size)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadCurrentReview() {
        currentReviewStartTime = timeUtils.now()
        val currentQueueItem = reviewItemsQueue.first()
        if (currentQueueItem.characterData.isCompleted) {
            applyNewReviewState(
                characterData = currentQueueItem.characterData.getCompleted(),
                history = currentQueueItem.history,
                progress = getUpdatedProgress()
            )
        } else {
            viewModelScope.launch {
                state.value = ScreenState.Loading
                val reviewData = currentQueueItem.characterData.await()
                applyNewReviewState(
                    characterData = reviewData,
                    history = currentQueueItem.history,
                    progress = getUpdatedProgress()
                )
            }
        }

        reviewItemsQueue.getOrNull(1)?.characterData?.start()
    }

    private fun getUpdatedProgress(): WritingPracticeProgress {
        val initialKanjiCount = practiceConfiguration.characterList.size

        val pendingCount = if (practiceConfiguration.isStudyMode) {
            reviewItemsQueue.count { it.history.size <= 2 }
        } else {
            reviewItemsQueue.count { it.history.size == 1 }
        }

        val repeatCount = reviewItemsQueue.size - pendingCount

        return WritingPracticeProgress(
            pendingCount = pendingCount,
            repeatCount = repeatCount,
            finishedCount = initialKanjiCount - pendingCount - repeatCount,
            totalReviews = totalReviewsCount++
        )
    }

    private fun applyNewReviewState(
        characterData: ReviewCharacterData,
        history: List<ReviewAction>,
        progress: WritingPracticeProgress
    ) {
        strokesCount[characterData.character] = characterData.strokes.size
        strokesQueue.addAll(characterData.strokes)

        val updatedReviewData = WritingReviewData(
            progress = progress,
            characterData = characterData,
            isStudyMode = history.last() == ReviewAction.Study,
        )

        val currentState = state.value as? ScreenState.Review
        if (currentState == null) {
            state.value = ScreenState.Review(
                configuration = screenConfiguration,
                reviewState = mutableStateOf(updatedReviewData)
            )
        } else {
            val reviewState = currentState.reviewState as MutableState
            reviewState.value = updatedReviewData
        }
    }

    private fun loadSummary() {
        viewModelScope.launch {
            val reviewResults = mistakesMap.map { (character, mistakes) ->
                WritingPracticeCharReviewResult(
                    character = character,
                    mistakes = mistakes
                )
            }
            state.value = ScreenState.Saving(
                reviewResultList = reviewResults,
                outcomeSelectionConfiguration = preferencesRepository.getWritingOutcomeSelectionConfiguration()
                    ?: OutcomeSelectionConfiguration(2)
            )
            reportReviewResult(reviewResults)
        }
    }

    private fun reportReviewResult(results: List<WritingPracticeCharReviewResult>) {
        val practiceDuration = reviewTimeMap.values.reduce { acc, duration -> acc.plus(duration) }
        analyticsManager.sendEvent("writing_practice_summary") {
            put("practice_size", results.size)
            put("total_mistakes", results.sumOf { it.mistakes })
            put("review_duration_sec", practiceDuration.inWholeSeconds)
        }
        results.forEach {
            analyticsManager.sendEvent("char_reviewed") {
                put("char", it.character)
                put("mistakes", it.mistakes)
            }
        }
    }

}
