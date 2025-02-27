package ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.use_case

import ua.syt0r.kanji.presentation.common.ScreenVocabPracticeType
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.VocabPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabPracticeQueueItemDescriptor

interface GetVocabPracticeQueueDataUseCase {
    suspend operator fun invoke(
        state: ScreenState.Configuration
    ): List<VocabPracticeQueueItemDescriptor>
}

class DefaultGetVocabPracticeQueueDataUseCase : GetVocabPracticeQueueDataUseCase {

    override suspend fun invoke(
        state: ScreenState.Configuration
    ): List<VocabPracticeQueueItemDescriptor> {
        return state.itemsSelectorState.result.asSequence()
            .map { (wordId, deckId) ->
                when (state.practiceType) {
                    ScreenVocabPracticeType.Flashcard -> {
                        VocabPracticeQueueItemDescriptor.Flashcard(
                            cardId = wordId,
                            deckId = deckId,
                            translationInFont = state.flashcard.translationInFront.value
                        )
                    }

                    ScreenVocabPracticeType.ReadingPicker -> {
                        VocabPracticeQueueItemDescriptor.ReadingPicker(
                            cardId = wordId,
                            deckId = deckId,
                            showMeaning = state.readingPicker.showMeaning.value
                        )
                    }

                    ScreenVocabPracticeType.Writing -> {
                        VocabPracticeQueueItemDescriptor.Writing(
                            cardId = wordId,
                            deckId = deckId
                        )
                    }
                }
            }
            .let { if (state.shuffle.value) it.shuffled() else it }
            .toList()
    }

}