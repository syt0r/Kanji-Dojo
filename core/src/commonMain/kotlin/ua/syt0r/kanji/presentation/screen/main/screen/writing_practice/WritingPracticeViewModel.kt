package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.app_state.AppStateManager
import ua.syt0r.kanji.core.app_state.CharacterProgressStatus
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.stroke_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.core.user_data.model.CharacterReviewOutcome
import ua.syt0r.kanji.core.user_data.model.CharacterWritingReviewResult
import ua.syt0r.kanji.core.user_data.model.OutcomeSelectionConfiguration
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeCharacterReviewResult
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeSavingResult
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewCharacterData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewUserAction
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.StrokeInputData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.StrokeProcessingResult
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeHintMode
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeProgress
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingScreenLayoutConfiguration
import java.util.LinkedList
import java.util.Queue
import kotlin.math.max
import kotlin.time.Duration

class WritingPracticeViewModel(
    private val viewModelScope: CoroutineScope,
    private val appStateManager: AppStateManager,
    private val loadDataUseCase: WritingPracticeScreenContract.LoadWritingPracticeDataUseCase,
    private val kanjiStrokeEvaluator: KanjiStrokeEvaluator,
    private val practiceRepository: PracticeRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val analyticsManager: AnalyticsManager,
    private val timeUtils: TimeUtils
) : WritingPracticeScreenContract.ViewModel {

    private enum class PracticeAction { Study, Review, Repeat }

    private data class ReviewQueueItem(
        val character: String,
        val characterData: Deferred<ReviewCharacterData>,
        val history: List<PracticeAction>
    )

    private companion object {
        private const val RepeatIndexShift = 2
    }

    private var practiceId: Long? = null
    private lateinit var screenConfiguration: WritingScreenConfiguration

    private val radicalsHighlight = mutableStateOf(false)

    private val reviewItemsQueue = LinkedList<ReviewQueueItem>()
    private val strokesQueue: Queue<Path> = LinkedList()
    private val completedItems = mutableMapOf<String, ReviewQueueItem>()

    private val mistakesMap = mutableMapOf<String, Int>()
    private val strokesCount = mutableMapOf<String, Int>()
    private val reviewTimeMap = mutableMapOf<String, Duration>()

    private lateinit var practiceStartTime: Instant
    private lateinit var currentReviewStartTime: Instant

    private var totalReviewsCount = 0

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun init(configuration: MainDestination.Practice.Writing) {
        if (practiceId != null) return

        practiceId = configuration.practiceId
        practiceStartTime = timeUtils.now()

        viewModelScope.launch {
            radicalsHighlight.value = preferencesRepository.getShouldHighlightRadicals()
            state.value = ScreenState.Configuring(
                characters = configuration.characterList,
                noTranslationsLayout = preferencesRepository.getNoTranslationsLayoutEnabled(),
                leftHandedMode = preferencesRepository.getLeftHandedModeEnabled(),
            )
        }
    }

    override fun onPracticeConfigured(configuration: WritingScreenConfiguration) {
        state.value = ScreenState.Loading
        screenConfiguration = configuration

        viewModelScope.launch {
            preferencesRepository.setNoTranslationsLayoutEnabled(configuration.noTranslationsLayout)
            preferencesRepository.setLeftHandedModeEnabled(configuration.leftHandedMode)

            val progresses = appStateManager.appStateFlow
                .filter { !it.isLoading }
                .first()
                .lastData!!
                .characterProgresses

            val queueItems = configuration.characters
                .let { if (configuration.shuffle) it.shuffled() else it }
                .map { character ->
                    val writingStatus = progresses[character]?.writingStatus
                    val shouldStudy: Boolean = when (configuration.hintMode) {
                        WritingPracticeHintMode.OnlyNew -> {
                            writingStatus == null || writingStatus == CharacterProgressStatus.New
                        }

                        WritingPracticeHintMode.All -> true
                        WritingPracticeHintMode.None -> false
                    }
                    val initialAction = when (shouldStudy) {
                        true -> PracticeAction.Study
                        false -> PracticeAction.Review
                    }
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

    override fun savePractice(result: PracticeSavingResult) {
        state.value = ScreenState.Loading
        viewModelScope.launch {
            preferencesRepository.setWritingOutcomeSelectionConfiguration(
                config = OutcomeSelectionConfiguration(result.toleratedMistakesCount)
            )
            val characterReviewList = mistakesMap.map { (character, mistakes) ->
                CharacterWritingReviewResult(
                    character = character,
                    practiceId = practiceId!!,
                    mistakes = mistakes,
                    reviewDuration = reviewTimeMap.getValue(character),
                    outcome = result.outcomes.getValue(character),
                    isStudy = completedItems.getValue(character)
                        .history.first() == PracticeAction.Study
                )
            }

            practiceRepository.saveWritingReviews(
                practiceTime = practiceStartTime,
                reviewResultList = characterReviewList
            )

            val totalStrokes = strokesCount.values.sum()
            val totalMistakes = characterReviewList.sumOf { it.mistakes }

            state.value = ScreenState.Saved(
                practiceDuration = reviewTimeMap.values.reduce { acc, duration -> acc.plus(duration) },
                accuracy = max(totalStrokes - totalMistakes, 0) / totalStrokes.toFloat() * 100,
                repeatCharacters = result.outcomes.filter { it.value == CharacterReviewOutcome.Fail }.keys.toList(),
                goodCharacters = result.outcomes.filter { it.value == CharacterReviewOutcome.Success }.keys.toList()
            )
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
                    history = queueItem.history.plus(PracticeAction.Review)
                )
                reviewItemsQueue.add(0, newItem)
            }

            ReviewUserAction.Repeat -> {
                val newReviewItem = queueItem.copy(
                    history = queueItem.history.plus(PracticeAction.Repeat)
                )
                val insertPosition = Integer.min(RepeatIndexShift, reviewItemsQueue.size)
                reviewItemsQueue.add(insertPosition, newReviewItem)
            }

            ReviewUserAction.Next -> {
                completedItems[queueItem.character] = queueItem
            }
        }

        if (reviewItemsQueue.isEmpty()) {
            loadSavingState()
        } else {
            loadCurrentReview()
        }
    }

    override fun toggleRadicalsHighlight() {
        val updatedValue = radicalsHighlight.value.not()
        radicalsHighlight.value = updatedValue
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
        val initialKanjiCount = screenConfiguration.characters.size

        val finishedCount = completedItems.size
        val repeatCount = reviewItemsQueue.count { it.history.last() == PracticeAction.Repeat }
        val pendingCount = initialKanjiCount - repeatCount - finishedCount

        return WritingPracticeProgress(
            pendingCount = pendingCount,
            repeatCount = repeatCount,
            finishedCount = finishedCount,
            totalReviews = totalReviewsCount++
        )
    }

    private fun applyNewReviewState(
        characterData: ReviewCharacterData,
        history: List<PracticeAction>,
        progress: WritingPracticeProgress
    ) {
        strokesCount[characterData.character] = characterData.strokes.size
        strokesQueue.addAll(characterData.strokes)

        val updatedReviewData = WritingReviewData(
            progress = progress,
            characterData = characterData,
            isStudyMode = history.last() == PracticeAction.Study,
        )

        val currentState = state.value as? ScreenState.Review
        if (currentState == null) {
            state.value = ScreenState.Review(
                shouldHighlightRadicals = radicalsHighlight,
                layoutConfiguration = WritingScreenLayoutConfiguration(
                    noTranslationsLayout = screenConfiguration.noTranslationsLayout,
                    radicalsHighlight = radicalsHighlight,
                    leftHandedMode = screenConfiguration.leftHandedMode
                ),
                reviewState = mutableStateOf(updatedReviewData)
            )
        } else {
            val reviewState = currentState.reviewState as MutableState
            reviewState.value = updatedReviewData
        }
    }

    private fun loadSavingState() {
        viewModelScope.launch {
            val reviewResults = mistakesMap.map { (character, mistakes) ->
                PracticeCharacterReviewResult(
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

    private fun reportReviewResult(results: List<PracticeCharacterReviewResult>) {
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
