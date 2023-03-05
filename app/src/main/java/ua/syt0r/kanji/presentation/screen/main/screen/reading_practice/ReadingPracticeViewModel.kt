package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSelectedOption
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSummaryItem
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingReviewCharacterData
import java.util.*
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class ReadingPracticeViewModel @Inject constructor(
    private val loadCharactersDataUseCase: ReadingPracticeContract.LoadCharactersDataUseCase,
    private val saveResultsUseCase: ReadingPracticeContract.SaveResultsUseCase,
    private val analyticsManager: AnalyticsManager
) : ViewModel(), ReadingPracticeContract.ViewModel {

    private lateinit var configuration: PracticeScreenConfiguration.Reading

    private data class QueueItem(
        val review: ReadingReviewCharacterData,
        val optionsHistory: List<ReadingPracticeSelectedOption>
    )

    private val queue = LinkedList<QueueItem>()
    private val completedItems = mutableMapOf<String, QueueItem>()

    private var totalReviewsCount = 0

    override val state: MutableState<ScreenState> = mutableStateOf(ScreenState.Loading)

    override fun initialize(configuration: PracticeScreenConfiguration.Reading) {
        if (::configuration.isInitialized) return
        this.configuration = configuration

        viewModelScope.launch {
            state.value = ScreenState.Loading
            state.value = withContext(Dispatchers.IO) {
                val items = loadCharactersDataUseCase.load(configuration)
                    .map { QueueItem(it, emptyList()) }
                queue.addAll(items)
                ScreenState.Review(
                    progress = getProgress(),
                    characterData = queue.peek()!!.review
                )
            }
        }
    }

    override fun select(option: ReadingPracticeSelectedOption) {
        val queueItem = queue.pop()
        val updatedQueueItem = queueItem.copy(
            optionsHistory = queueItem.optionsHistory.plus(option)
        )

        when (option) {
            ReadingPracticeSelectedOption.Good -> {
                completedItems[updatedQueueItem.review.character] = updatedQueueItem
                if (queue.isEmpty()) {
                    loadSummary()
                } else {
                    state.value = ScreenState.Review(
                        progress = getProgress(),
                        characterData = queue.peek()!!.review
                    )
                }
            }
            ReadingPracticeSelectedOption.Repeat -> {
                val insertPosition = min(3, queue.size)
                queue.add(insertPosition, updatedQueueItem)
                state.value = ScreenState.Review(
                    progress = getProgress(),
                    characterData = queue.peek()!!.review
                )
            }
        }

    }

    override fun reportScreenShown(configuration: PracticeScreenConfiguration.Reading) {
        analyticsManager.setScreen("reading_practice")
    }

    private fun getProgress(): ReadingPracticeContract.ReviewProgress {
        val completed = configuration.characterList.size - queue.size
        val repeat = queue.count { it.optionsHistory.isNotEmpty() }
        val pending = queue.count { it.optionsHistory.isEmpty() }
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
                        repeats = queueItem.optionsHistory.size - 1
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