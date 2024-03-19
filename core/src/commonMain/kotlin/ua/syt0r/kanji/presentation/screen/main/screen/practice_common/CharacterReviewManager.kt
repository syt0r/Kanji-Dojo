package ua.syt0r.kanji.presentation.screen.main.screen.practice_common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Instant
import ua.syt0r.kanji.core.debounceFirst
import ua.syt0r.kanji.core.time.TimeUtils
import java.util.LinkedList
import kotlin.math.min
import kotlin.time.Duration

interface CharacterReviewManager<HistoryStatus, CharacterDetails, CharacterReviewSummary> {

    val currentItem: StateFlow<CharacterReviewData<HistoryStatus, CharacterDetails>>

    suspend fun next(action: ReviewAction<HistoryStatus>)

    fun getProgress(): PracticeProgress
    fun getSummary(): ReviewSummary<CharacterReviewSummary>

}

sealed interface ReviewAction<T> {
    class Next<T> : ReviewAction<T>
    class RepeatNow<T>(val status: T) : ReviewAction<T>
    class RepeatLater<T>(val status: T) : ReviewAction<T>
}

data class CharacterReviewData<HistoryStatus, CharacterDetails>(
    val character: String,
    val details: Deferred<CharacterDetails>,
    val history: List<HistoryStatus>
)

data class PracticeProgress(
    val pendingCount: Int,
    val repeatCount: Int,
    val finishedCount: Int,
    val totalReviews: Int
)

data class ReviewSummary<T>(
    val startTime: Instant,
    val characterSummaries: Map<String, SummaryCharacterData<T>>,
    val totalReviewTime: Duration
)

data class SummaryCharacterData<T>(
    val reviewDuration: Duration,
    val details: T
)

abstract class BaseCharacterReviewManager<HistoryStatus, CharacterDetails, CharacterReviewSummary>(
    reviewItems: List<CharacterReviewData<HistoryStatus, CharacterDetails>>,
    coroutineScope: CoroutineScope,
    private val timeUtils: TimeUtils,
    private val onCompletedCallback: () -> Unit
) : CharacterReviewManager<HistoryStatus, CharacterDetails, CharacterReviewSummary> {

    companion object {
        private const val RepeatIndexShift = 2
    }

    private val queue = LinkedList(reviewItems)
    private val completedItems =
        mutableListOf<Pair<String, SummaryCharacterData<CharacterReviewSummary>>>()

    private val reviewActionsQueue = Channel<ReviewAction<HistoryStatus>>()

    private val reviewCharactersCount = reviewItems.size
    private var totalReviewsCount = 0

    private val practiceStartTime: Instant = timeUtils.now()
    private var currentReviewStartTime: Instant = timeUtils.now()

    private val reviewTimeMap = mutableMapOf<String, Duration>()

    private val internalCurrentItem = MutableStateFlow(queue.peek())
    override val currentItem: StateFlow<CharacterReviewData<HistoryStatus, CharacterDetails>>
        get() = internalCurrentItem

    init {
        startNextItemPreloading()
        reviewActionsQueue.consumeAsFlow()
            .debounceFirst()
            .onEach(::handleAction)
            .launchIn(coroutineScope)
    }

    protected abstract fun getCharacterSummary(
        history: List<HistoryStatus>,
        characterData: CharacterDetails
    ): CharacterReviewSummary

    abstract fun isRepeat(
        data: CharacterReviewData<HistoryStatus, CharacterDetails>
    ): Boolean

    override suspend fun next(action: ReviewAction<HistoryStatus>) {
        reviewActionsQueue.send(action)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun next() {
        // Ignore handling if queue is empty, happens when user rapidly clicks on last button
        val characterData = queue.poll() ?: return
        addCharacterReviewDuration(characterData.character)

        val summaryCharacterData = SummaryCharacterData(
            reviewDuration = reviewTimeMap.getValue(characterData.character),
            details = getCharacterSummary(
                history = characterData.history,
                characterData = characterData.details.getCompleted()
            )
        )
        completedItems.add(characterData.character to summaryCharacterData)

        updateState()
    }

    private fun reviewNow(status: HistoryStatus) {
        val updatedCharacterData = queue.poll().run {
            copy(history = history.plus(status))
        }
        queue.add(0, updatedCharacterData)

        addCharacterReviewDuration(updatedCharacterData.character)
        updateState()
    }

    private fun scheduleReview(status: HistoryStatus) {
        val updatedCharacterData = queue.poll().run {
            copy(history = history.plus(status))
        }
        val insertPosition = min(RepeatIndexShift, queue.size)
        queue.add(insertPosition, updatedCharacterData)

        addCharacterReviewDuration(updatedCharacterData.character)
        updateState()
    }

    private fun addCharacterReviewDuration(character: String) {
        val now = timeUtils.now()
        reviewTimeMap[character] = reviewTimeMap
            .getOrDefault(character, Duration.ZERO)
            .plus(now.minus(currentReviewStartTime))
        currentReviewStartTime = timeUtils.now()
    }

    private fun updateState() {
        if (queue.isEmpty()) {
            onCompletedCallback()
        } else {
            queue.peek()?.let { internalCurrentItem.value = it }
            startNextItemPreloading()
        }
    }

    private fun startNextItemPreloading() {
        queue.getOrNull(1)?.details?.start()
    }

    private fun handleAction(action: ReviewAction<HistoryStatus>) {
        when (action) {
            is ReviewAction.Next -> next()
            is ReviewAction.RepeatNow -> reviewNow(action.status)
            is ReviewAction.RepeatLater -> scheduleReview(action.status)
        }
    }

    override fun getProgress(): PracticeProgress {
        val finishedCount = completedItems.size
        val repeatCount = queue.count { isRepeat(it) }
        val pendingCount = reviewCharactersCount - repeatCount - finishedCount

        return PracticeProgress(
            pendingCount = pendingCount,
            repeatCount = repeatCount,
            finishedCount = finishedCount,
            totalReviews = totalReviewsCount++
        )
    }

    override fun getSummary(): ReviewSummary<CharacterReviewSummary> {
        return ReviewSummary(
            startTime = practiceStartTime,
            totalReviewTime = reviewTimeMap.values.fold(Duration.ZERO) { a, b -> a.plus(b) },
            characterSummaries = completedItems.toMap()
        )
    }

}