package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSelectedOption
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSummaryItem
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingReviewCharacterData

interface ReadingPracticeContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun initialize(configuration: MainDestination.Practice.Reading)
        fun select(option: ReadingPracticeSelectedOption)

        fun reportScreenShown(configuration: MainDestination.Practice.Reading)

    }

    data class ReviewProgress(
        val pending: Int,
        val repeat: Int,
        val completed: Int,
        val totalReviewsCount: Int = 0
    )

    sealed interface ScreenState {

        object Loading : ScreenState

        data class Review(
            val progress: ReviewProgress,
            val characterData: ReadingReviewCharacterData
        ) : ScreenState

        data class Summary(
            val items: List<ReadingPracticeSummaryItem>
        ) : ScreenState

    }

    interface LoadCharactersDataUseCase {
        suspend fun load(
            configuration: MainDestination.Practice.Reading
        ): List<ReadingReviewCharacterData>
    }

    interface SaveResultsUseCase {
        suspend fun save(practiceId: Long, summary: ScreenState.Summary)
    }

}