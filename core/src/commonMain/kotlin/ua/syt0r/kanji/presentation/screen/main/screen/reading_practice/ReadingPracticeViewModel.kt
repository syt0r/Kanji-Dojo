package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.time.TimeUtils
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.core.user_data.model.CharacterReadingReviewResult
import ua.syt0r.kanji.core.user_data.model.CharacterReviewOutcome
import ua.syt0r.kanji.core.user_data.model.OutcomeSelectionConfiguration
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeCharacterReviewResult
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeSavingResult
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSelectedOption
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingReviewCharacterData
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingScreenConfiguration
import java.util.LinkedList
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

class ReadingPracticeViewModel(
    private val viewModelScope: CoroutineScope,
    private val loadCharactersDataUseCase: ReadingPracticeContract.LoadCharactersDataUseCase,
    private val preferencesRepository: UserPreferencesRepository,
    private val practiceRepository: PracticeRepository,
    private val analyticsManager: AnalyticsManager,
    private val timeUtils: TimeUtils
) : ReadingPracticeContract.ViewModel {

    private lateinit var practiceConfiguration: MainDestination.Practice.Reading

    private data class QueueItem(
        val character: String,
        val data: Deferred<ReadingReviewCharacterData>,
        val history: List<ReadingPracticeSelectedOption>
    )

    private val queue = LinkedList<QueueItem>()
    private val completedItems = mutableMapOf<String, QueueItem>()

    private val reviewTimeMap = mutableMapOf<String, Duration>()
    private lateinit var currentReviewStartTime: Instant

    private var totalReviewsCount = 0

    override val state: MutableState<ScreenState> = mutableStateOf(ScreenState.Loading)

    override fun initialize(configuration: MainDestination.Practice.Reading) {
        if (::practiceConfiguration.isInitialized) return
        this.practiceConfiguration = configuration

        state.value = ScreenState.Configuration(
            characters = configuration.characterList,
            configuration = ReadingScreenConfiguration(shuffle = true)
        )
    }

    override fun onConfigured(configuration: ReadingScreenConfiguration) {
        state.value = ScreenState.Loading
        viewModelScope.launch {
            val characterList = practiceConfiguration.characterList
                .let { if (configuration.shuffle) it.shuffled() else it }

            val items = characterList.map { character ->
                QueueItem(
                    character = character,
                    data = async(
                        context = Dispatchers.IO,
                        start = CoroutineStart.LAZY
                    ) {
                        loadCharactersDataUseCase.load(character)
                    },
                    history = emptyList()
                )
            }

            queue.addAll(items)
            loadCurrentReviewItem()
        }
    }

    override fun select(option: ReadingPracticeSelectedOption) {
        if (queue.isEmpty()) return // Skips rapid click on buttons when transitioning to summary

        val queueItem = queue.pop()
        val updatedQueueItem = queueItem.copy(history = queueItem.history.plus(option))

        val currentCharacter = queueItem.character
        val reviewDuration = timeUtils.now() - currentReviewStartTime
        reviewTimeMap[currentCharacter] = reviewTimeMap[currentCharacter]
            ?.plus(reviewDuration)
            ?: reviewDuration

        when (option) {
            ReadingPracticeSelectedOption.Good -> {
                completedItems[updatedQueueItem.character] = updatedQueueItem
                if (queue.isEmpty()) {
                    loadSavingState()
                } else {
                    loadCurrentReviewItem()
                }
            }

            ReadingPracticeSelectedOption.Repeat -> {
                val insertPosition = min(3, queue.size)
                queue.add(insertPosition, updatedQueueItem)
                loadCurrentReviewItem()
            }
        }

    }

    override fun savePractice(result: PracticeSavingResult) {
        val currentState = state.value as ScreenState.Saving
        state.value = ScreenState.Loading
        viewModelScope.launch {
            savePracticeInternal(result)

            val totalCharacter = practiceConfiguration.characterList.size
            val totalMistakes = currentState.reviewResultList.asSequence()
                .map { it.mistakes }
                .reduce { acc, mistakes -> acc.plus(mistakes) }

            state.value = ScreenState.Saved(
                practiceDuration = reviewTimeMap.values
                    .reduce { acc, duration -> acc.plus(duration) },
                accuracy = max(totalCharacter - totalMistakes, 0) / totalCharacter.toFloat() * 100,
                repeatCharacters = result.outcomes
                    .filter { it.value == CharacterReviewOutcome.Fail }
                    .keys
                    .toList(),
                goodCharacters = result.outcomes
                    .filter { it.value == CharacterReviewOutcome.Success }
                    .keys
                    .toList()
            )
        }
    }

    override fun reportScreenShown(configuration: MainDestination.Practice.Reading) {
        analyticsManager.setScreen("reading_practice")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadCurrentReviewItem() {
        currentReviewStartTime = timeUtils.now()
        val queueItem = queue.peek()!!
        val data = queueItem.data
        if (data.isCompleted) {
            state.value = ScreenState.Review(
                progress = getProgress(),
                characterData = data.getCompleted()
            )
        } else {
            viewModelScope.launch {
                state.value = ScreenState.Loading
                state.value = ScreenState.Review(
                    progress = getProgress(),
                    characterData = data.await()
                )
            }
        }
        queue.getOrNull(1)?.data?.start()
    }

    private fun getProgress(): ReadingPracticeContract.ReviewProgress {
        val completed = practiceConfiguration.characterList.size - queue.size
        val repeat = queue.count { it.history.isNotEmpty() }
        val pending = queue.count { it.history.isEmpty() }
        return ReadingPracticeContract.ReviewProgress(
            pending = pending,
            repeat = repeat,
            completed = completed,
            totalReviewsCount = totalReviewsCount++
        )
    }

    private fun loadSavingState() {
        state.value = ScreenState.Loading
        viewModelScope.launch {
            state.value = withContext(Dispatchers.IO) {
                ScreenState.Saving(
                    outcomeSelectionConfiguration = preferencesRepository.getReadingOutcomeSelectionConfiguration()
                        ?: OutcomeSelectionConfiguration(0),
                    reviewResultList = completedItems.map {
                        PracticeCharacterReviewResult(
                            character = it.key,
                            mistakes = it.value.history.size - 1
                        )
                    }
                )
            }
        }
    }

    private suspend fun savePracticeInternal(result: PracticeSavingResult) {
        preferencesRepository.setReadingOutcomeSelectionConfiguration(
            config = OutcomeSelectionConfiguration(result.toleratedMistakesCount)
        )
        val now = timeUtils.now()
        practiceRepository.saveReadingReviews(
            practiceTime = now,
            reviewResultList = result.outcomes.map { (character, outcome) ->
                CharacterReadingReviewResult(
                    character = character,
                    practiceId = practiceConfiguration.practiceId,
                    mistakes = completedItems.getValue(character).history.size - 1,
                    reviewDuration = reviewTimeMap.getValue(character),
                    outcome = outcome
                )
            }
        )
    }

}