package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSelectedOption
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSummaryItem
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingReviewCharacterData
import java.util.*
import kotlin.math.min

class ReadingPracticeViewModel(
    private val viewModelScope: CoroutineScope,
    private val loadCharactersDataUseCase: ReadingPracticeContract.LoadCharactersDataUseCase,
    private val saveResultsUseCase: ReadingPracticeContract.SaveResultsUseCase,
    private val analyticsManager: AnalyticsManager
) : ReadingPracticeContract.ViewModel {

    private lateinit var configuration: MainDestination.Practice.Reading

    private data class QueueItem(
        val character: String,
        val data: Deferred<ReadingReviewCharacterData>,
        val history: List<ReadingPracticeSelectedOption>
    )

    private val queue = LinkedList<QueueItem>()
    private val completedItems = mutableMapOf<String, QueueItem>()

    private var totalReviewsCount = 0

    override val state: MutableState<ScreenState> = mutableStateOf(ScreenState.Loading)

    override fun initialize(configuration: MainDestination.Practice.Reading) {
        if (::configuration.isInitialized) return
        this.configuration = configuration

        viewModelScope.launch {
            state.value = ScreenState.Loading

            val items = configuration.characterList.map { character ->
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

        when (option) {
            ReadingPracticeSelectedOption.Good -> {
                completedItems[updatedQueueItem.character] = updatedQueueItem
                if (queue.isEmpty()) {
                    loadSummary()
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

    override fun reportScreenShown(configuration: MainDestination.Practice.Reading) {
        analyticsManager.setScreen("reading_practice")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadCurrentReviewItem() {
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
        val completed = configuration.characterList.size - queue.size
        val repeat = queue.count { it.history.isNotEmpty() }
        val pending = queue.count { it.history.isEmpty() }
        return ReadingPracticeContract.ReviewProgress(
            pending = pending,
            repeat = repeat,
            completed = completed,
            totalReviewsCount = totalReviewsCount++
        )
    }

    private fun loadSummary() {
        state.value = ScreenState.Loading
        viewModelScope.launch {
            val summaryState = withContext(Dispatchers.IO) {
                val items = completedItems.map { (character, queueItem) ->
                    ReadingPracticeSummaryItem(
                        character = character,
                        repeats = queueItem.history.size - 1
                    )
                }
                ScreenState.Summary(items).also {
                    saveResultsUseCase.save(configuration.practiceId, it)
                }
            }
            state.value = summaryState
        }
    }

}