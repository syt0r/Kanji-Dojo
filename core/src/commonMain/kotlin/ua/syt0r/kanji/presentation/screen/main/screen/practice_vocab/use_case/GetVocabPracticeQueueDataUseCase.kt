package ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.use_case

import ua.syt0r.kanji.core.srs.SrsCardRepository
import ua.syt0r.kanji.core.user_data.preferences.PreferencesNewCardsOrder
import ua.syt0r.kanji.presentation.common.ScreenVocabPracticeType
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.VocabPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabPracticeCardConfigurationData
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabPracticeQueueItemDescriptor
import ua.syt0r.kanji.presentation.screen.main.screen.practice_vocab.data.VocabPracticeScreenConfiguration

interface GetVocabPracticeQueueDataUseCase {
    suspend operator fun invoke(
        configuration: VocabPracticeScreenConfiguration,
        configuratedState: ScreenState.Configuration
    ): List<VocabPracticeQueueItemDescriptor>
}

class DefaultGetVocabPracticeQueueDataUseCase(
    private val srsCardRepository: SrsCardRepository
) : GetVocabPracticeQueueDataUseCase {

    override suspend fun invoke(
        configuration: VocabPracticeScreenConfiguration,
        configuratedState: ScreenState.Configuration
    ): List<VocabPracticeQueueItemDescriptor> {
        return configuration.cards
            .let {
                val srsCardCache = srsCardRepository.getAll()
                val defaultCardsOrder = configuration.cards.map {
                    val srsKey = configuration.practiceType.dataType.toSrsKey(it.cardId)
                    VocabPracticeCardConfigurationData(
                        cardId = it.cardId,
                        deckId = it.deckId,
                        srsCardKey = srsKey,
                        isNew = srsCardCache.contains(srsKey).not()
                    )
                }

                val newCards = defaultCardsOrder.filter { it.isNew }
                val nonNewCards = defaultCardsOrder.filterNot { it.isNew }

                val selectorState = configuratedState.cardsSelectorState
                val shuffle = selectorState.shuffle.value
                val newCardsOrder = selectorState.newCardsOrder.value

                fun <T> List<T>.withShuffleApplied(): List<T> =
                    if (shuffle) shuffled() else this

                when (newCardsOrder) {
                    PreferencesNewCardsOrder.First -> newCards.withShuffleApplied()
                        .plus(nonNewCards.withShuffleApplied())

                    PreferencesNewCardsOrder.Last -> nonNewCards.withShuffleApplied()
                        .plus(newCards.withShuffleApplied())

                    PreferencesNewCardsOrder.Mixed -> defaultCardsOrder.withShuffleApplied()
                }
            }
            .take(configuratedState.cardsSelectorState.selectedCountIntState.value)
            .asSequence()
            .map { (wordId, deckId) ->
                when (configuratedState.practiceType) {
                    ScreenVocabPracticeType.Flashcard -> {
                        VocabPracticeQueueItemDescriptor.Flashcard(
                            cardId = wordId,
                            deckId = deckId,
                            translationInFont = configuratedState.flashcard.translationInFront.value
                        )
                    }

                    ScreenVocabPracticeType.ReadingPicker -> {
                        VocabPracticeQueueItemDescriptor.ReadingPicker(
                            cardId = wordId,
                            deckId = deckId,
                            showMeaning = configuratedState.readingPicker.showMeaning.value
                        )
                    }

                    ScreenVocabPracticeType.Writing -> {
                        VocabPracticeQueueItemDescriptor.Writing(
                            cardId = wordId,
                            deckId = deckId,
                            showKanaReading = configuratedState.writing.showKanaReading.value
                        )
                    }
                }
            }
            .toList()
    }

}