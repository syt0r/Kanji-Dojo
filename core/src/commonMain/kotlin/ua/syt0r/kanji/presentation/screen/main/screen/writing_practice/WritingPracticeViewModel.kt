package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.stroke_evaluator.AltKanjiStrokeEvaluator
import ua.syt0r.kanji.core.stroke_evaluator.DefaultKanjiStrokeEvaluator
import ua.syt0r.kanji.core.stroke_evaluator.KanjiStrokeEvaluator
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.tts.KanaTtsManager
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.core.user_data.model.CharacterReviewOutcome
import ua.syt0r.kanji.core.user_data.model.CharacterWritingReviewResult
import ua.syt0r.kanji.core.user_data.model.OutcomeSelectionConfiguration
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeCharacterReviewResult
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeSavingResult
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.ReviewAction
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.ReviewSummary
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewUserAction
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewUserAction.Next
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewUserAction.Repeat
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewUserAction.StudyNext
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.StrokeInputData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.StrokeProcessingResult
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewCharacterDetails
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewCharacterSummaryDetails
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingScreenLayoutConfiguration
import kotlin.math.max


class WritingPracticeViewModel(
    private val viewModelScope: CoroutineScope,
    private val loadDataUseCase: WritingPracticeScreenContract.LoadPracticeData,
    private val practiceRepository: PracticeRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val analyticsManager: AnalyticsManager,
    private val timeUtils: TimeUtils,
    private val kanaTtsManager: KanaTtsManager
) : WritingPracticeScreenContract.ViewModel {

    private var practiceId: Long? = null
    private lateinit var screenConfiguration: WritingScreenConfiguration

    private val radicalsHighlight = mutableStateOf(false)
    private val kanaAutoPlay = mutableStateOf(true)

    private lateinit var reviewManager: WritingCharacterReviewManager
    private lateinit var reviewDataState: MutableStateFlow<WritingReviewData>

    private val mistakesMap = mutableMapOf<String, Int>()

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)
    private lateinit var kanjiStrokeEvaluator: KanjiStrokeEvaluator

    override fun init(configuration: MainDestination.Practice.Writing) {
        if (practiceId != null) return

        practiceId = configuration.practiceId

        viewModelScope.launch {
            radicalsHighlight.value = preferencesRepository.getShouldHighlightRadicals()
            state.value = ScreenState.Configuring(
                characters = configuration.characterList,
                noTranslationsLayout = preferencesRepository.getNoTranslationsLayoutEnabled(),
                leftHandedMode = preferencesRepository.getLeftHandedModeEnabled(),
                altStrokeEvaluatorEnabled = preferencesRepository.getAltStrokeEvaluatorEnabled(),
            )
        }
    }

    override fun onPracticeConfigured(configuration: WritingScreenConfiguration) {
        state.value = ScreenState.Loading
        screenConfiguration = configuration

        viewModelScope.launch {
            preferencesRepository.setNoTranslationsLayoutEnabled(configuration.noTranslationsLayout)
            preferencesRepository.setLeftHandedModeEnabled(configuration.leftHandedMode)
            preferencesRepository.setAltStrokeEvaluatorEnabled(configuration.altStrokeEvaluatorEnabled)

            kanjiStrokeEvaluator = if (configuration.altStrokeEvaluatorEnabled)
                AltKanjiStrokeEvaluator()
            else
                DefaultKanjiStrokeEvaluator()

            val queueItems = loadDataUseCase.load(configuration, viewModelScope)
            reviewManager = WritingCharacterReviewManager(
                reviewItems = queueItems,
                timeUtils = timeUtils,
                onCompletedCallback = { loadSavingState() }
            )

            reviewManager.currentItem
                .onEach { it.applyToState() }
                .launchIn(this)
        }
    }

    override suspend fun submitUserDrawnPath(inputData: StrokeInputData): StrokeProcessingResult {
        val isDrawnCorrectly = withContext(Dispatchers.IO) {
            kanjiStrokeEvaluator.areStrokesSimilar(inputData.kanjiPath, inputData.userPath)
        }
        val result = if (isDrawnCorrectly) {
            reviewDataState.value.drawnStrokesCount.value += 1
            StrokeProcessingResult.Correct(
                userPath = inputData.userPath,
                kanjiPath = inputData.kanjiPath
            )
        } else {
            val currentStrokeMistakes = reviewDataState.value.run {
                currentStrokeMistakes.value += 1
                currentCharacterMistakes.value += 1
                currentStrokeMistakes.value
            }
            val path = if (currentStrokeMistakes > 2) inputData.kanjiPath else inputData.userPath
            StrokeProcessingResult.Mistake(path)
        }
        return result
    }

    override fun savePractice(result: PracticeSavingResult) {
        state.value = ScreenState.Loading
        viewModelScope.launch {
            preferencesRepository.setWritingOutcomeSelectionConfiguration(
                config = OutcomeSelectionConfiguration(result.toleratedMistakesCount)
            )

            val reviewSummary = reviewManager.getSummary()

            val characterReviewList = mistakesMap.map { (character, mistakes) ->
                val characterReviewSummary = reviewSummary.characterSummaries.getValue(character)
                CharacterWritingReviewResult(
                    character = character,
                    practiceId = practiceId!!,
                    mistakes = mistakes,
                    reviewDuration = characterReviewSummary.reviewDuration,
                    outcome = result.outcomes.getValue(character),
                    isStudy = characterReviewSummary.details.isStudy
                )
            }

            practiceRepository.saveWritingReviews(
                practiceTime = reviewSummary.startTime,
                reviewResultList = characterReviewList
            )

            val totalStrokes = reviewSummary.characterSummaries.values
                .fold(0) { a, b -> a + b.details.strokesCount }
            val totalMistakes = characterReviewList.sumOf { it.mistakes }

            state.value = ScreenState.Saved(
                practiceDuration = reviewSummary.totalReviewTime,
                accuracy = max(totalStrokes - totalMistakes, 0) / totalStrokes.toFloat() * 100,
                repeatCharacters = result.outcomes.filter { it.value == CharacterReviewOutcome.Fail }.keys.toList(),
                goodCharacters = result.outcomes.filter { it.value == CharacterReviewOutcome.Success }.keys.toList()
            )

            reportReviewResult(reviewSummary)
        }
    }

    override fun onHintClick() {
        reviewDataState.value.run {
            currentStrokeMistakes.value += 1
            currentCharacterMistakes.value += 1
        }
    }

    override fun loadNextCharacter(userAction: ReviewUserAction) {
        val (character, mistakes) = reviewDataState.value
            .run { characterData.character to currentCharacterMistakes.value }
        mistakesMap[character] = mistakesMap.getOrDefault(character, 0) + mistakes

        val action = when (userAction) {
            Next -> ReviewAction.Next()
            StudyNext -> ReviewAction.RepeatNow(WritingCharacterReviewHistory.Review)
            Repeat -> ReviewAction.RepeatLater(WritingCharacterReviewHistory.Repeat)
        }
        reviewManager.next(action)
    }

    override fun toggleRadicalsHighlight() {
        val updatedValue = radicalsHighlight.value.not()
        radicalsHighlight.value = updatedValue
        viewModelScope.launch {
            preferencesRepository.setShouldHighlightRadicals(updatedValue)
        }
    }

    override fun toggleAutoPlay() {
        kanaAutoPlay.value = !kanaAutoPlay.value
    }

    override fun speakRomaji(romaji: String) {
        viewModelScope.launch { kanaTtsManager.speak(romaji) }
    }

    override fun reportScreenShown(configuration: MainDestination.Practice.Writing) {
        analyticsManager.setScreen("writing_practice")
        analyticsManager.sendEvent("writing_practice_configuration") {
            put("list_size", configuration.characterList.size)
        }
    }

    private suspend fun WritingCharacterReviewData.applyToState() {
        if (!details.isCompleted) {
            state.value = ScreenState.Loading
        }

        val finalDetails = details.await()

        val writingReviewData = WritingReviewData(
            progress = reviewManager.getProgress(),
            characterData = finalDetails,
            isStudyMode = history.last() == WritingCharacterReviewHistory.Study,
            drawnStrokesCount = mutableStateOf(0),
            currentStrokeMistakes = mutableStateOf(0),
            currentCharacterMistakes = mutableStateOf(0)
        )

        if (!::reviewDataState.isInitialized) {
            reviewDataState = MutableStateFlow(writingReviewData)
        } else {
            reviewDataState.value = writingReviewData
        }

        val currentState = state.value
        if (currentState !is ScreenState.Review) {
            state.value = ScreenState.Review(
                layoutConfiguration = WritingScreenLayoutConfiguration(
                    noTranslationsLayout = screenConfiguration.noTranslationsLayout,
                    radicalsHighlight = radicalsHighlight,
                    kanaAutoPlay = kanaAutoPlay,
                    leftHandedMode = screenConfiguration.leftHandedMode
                ),
                reviewState = reviewDataState
            )
        }

        if (kanaAutoPlay.value && finalDetails is WritingReviewCharacterDetails.KanaReviewDetails) {
            delay(200)
            kanaTtsManager.speak(finalDetails.romaji)
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
        }
    }

    private fun reportReviewResult(
        reviewSummary: ReviewSummary<WritingReviewCharacterSummaryDetails>
    ) {
        analyticsManager.sendEvent("writing_practice_summary") {
            put("practice_size", reviewSummary.characterSummaries.size)
            put("total_mistakes", mistakesMap.values.sum())
            put("review_duration_sec", reviewSummary.totalReviewTime.inWholeSeconds)
        }
        reviewSummary.characterSummaries.forEach { (character, _) ->
            analyticsManager.sendEvent("char_reviewed") {
                put("char", character)
                put("mistakes", mistakesMap.getValue(character))
            }
        }
    }

}
