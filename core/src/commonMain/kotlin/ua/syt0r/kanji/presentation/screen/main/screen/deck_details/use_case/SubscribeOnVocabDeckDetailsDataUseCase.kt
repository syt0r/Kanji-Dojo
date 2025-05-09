package ua.syt0r.kanji.presentation.screen.main.screen.deck_details.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.RefreshableData
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.app_data.data.formattedVocabStringReading
import ua.syt0r.kanji.core.logger.runWithTimeLog
import ua.syt0r.kanji.core.refreshableDataFlow
import ua.syt0r.kanji.core.srs.VocabSrsManager
import ua.syt0r.kanji.core.user_data.database.VocabPracticeRepository
import ua.syt0r.kanji.presentation.LifecycleState
import ua.syt0r.kanji.presentation.common.ScreenVocabPracticeType
import ua.syt0r.kanji.presentation.screen.main.screen.deck_details.data.DeckDetailsData
import ua.syt0r.kanji.presentation.screen.main.screen.deck_details.data.DeckDetailsItemData
import ua.syt0r.kanji.presentation.screen.main.screen.deck_details.data.DeckDetailsScreenConfiguration
import ua.syt0r.kanji.vocab_card_missing_meaning
import kotlin.coroutines.CoroutineContext

interface SubscribeOnVocabDeckDetailsDataUseCase {
    operator fun invoke(
        configuration: DeckDetailsScreenConfiguration.VocabDeck,
        lifecycleState: StateFlow<LifecycleState>
    ): Flow<RefreshableData<DeckDetailsData.VocabDeckData>>
}

class DefaultSubscribeOnVocabDeckDetailsDataUseCase(
    private val vocabSrsManager: VocabSrsManager,
    private val vocabPracticeRepository: VocabPracticeRepository,
    private val appDataRepository: AppDataRepository,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : SubscribeOnVocabDeckDetailsDataUseCase {

    override operator fun invoke(
        configuration: DeckDetailsScreenConfiguration.VocabDeck,
        lifecycleState: StateFlow<LifecycleState>,
    ): Flow<RefreshableData<DeckDetailsData.VocabDeckData>> {
        return refreshableDataFlow(
            dataChangeFlow = merge(
                vocabSrsManager.dataChangeFlow,
                vocabPracticeRepository.changesFlow
            ),
            lifecycleState = lifecycleState,
            valueProvider = {
                runWithTimeLog("vocabDeckData") { getUpdatedData(configuration) }
            }
        )
    }

    private suspend fun getUpdatedData(
        configuration: DeckDetailsScreenConfiguration.VocabDeck
    ): DeckDetailsData.VocabDeckData = withContext(coroutineContext) {
        val deckInfo = vocabSrsManager.getDeck(configuration.deckId)
        val vocabCards = vocabPracticeRepository.getAllCards()
            .associateBy { it.cardId }

        val deckCards = deckInfo.items.map { vocabCards.getValue(it) }
        val wordsWithNoMeanings = deckCards.filter { it.data.meaning == null }
            .mapNotNull { it.data.dictionaryId }
            .toSet()

        val extraMeanings = appDataRepository.getWordSenses(wordsWithNoMeanings)
            .associateBy { it.wordId }

        DeckDetailsData.VocabDeckData(
            deckTitle = deckInfo.title,
            items = deckCards.mapIndexed { index, card ->
                val reading = card.data.run {
                    formattedVocabStringReading(kanaReading, kanjiReading)
                }

                val meaning = card.data.meaning
                    ?: card.data.dictionaryId?.let {
                        runCatching {
                            extraMeanings.getValue(it).getMatchingMeaning(
                                kanjiReading = card.data.kanjiReading,
                                kanaReading = card.data.kanaReading
                            )
                        }.getOrNull()
                    }
                    ?: getString(Res.string.vocab_card_missing_meaning)

                DeckDetailsItemData.VocabData(
                    reading = reading,
                    meaning = meaning,
                    card = card,
                    positionInPractice = index,
                    srsStatus = ScreenVocabPracticeType.entries.associateWith {
                        deckInfo.progressMap.getValue(it.dataType).itemsData
                            .getValue(card.cardId).status
                    }
                )
            }
        )
    }

}