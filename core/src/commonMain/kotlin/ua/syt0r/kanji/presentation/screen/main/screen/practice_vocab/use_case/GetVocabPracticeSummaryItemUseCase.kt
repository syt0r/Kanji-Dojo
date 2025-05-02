package ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.use_case

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabPracticeItemData
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabPracticeQueueItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabSummaryItem

interface GetVocabPracticeSummaryItemUseCase {
    operator fun invoke(item: VocabPracticeQueueItem, totalReviews: Deferred<Int>): VocabSummaryItem
}

class DefaultGetVocabPracticeSummaryItemUseCase : GetVocabPracticeSummaryItemUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(item: VocabPracticeQueueItem, totalReviews: Deferred<Int>): VocabSummaryItem {
        return when (val state = item.data.getCompleted()) {
            is VocabPracticeItemData.Flashcard -> VocabSummaryItem(
                reading = state.reading,
                vocabReference = state.vocabReference,
                totalReviews = totalReviews,
                nextInterval = item.srsCard.interval
            )

            is VocabPracticeItemData.Reading -> VocabSummaryItem(
                reading = state.revealedReading,
                vocabReference = state.vocabReference,
                totalReviews = totalReviews,
                nextInterval = item.srsCard.interval
            )

            is VocabPracticeItemData.Writing -> VocabSummaryItem(
                reading = state.summaryReading,
                vocabReference = state.vocabReference,
                totalReviews = totalReviews,
                nextInterval = item.srsCard.interval
            )
        }
    }

}