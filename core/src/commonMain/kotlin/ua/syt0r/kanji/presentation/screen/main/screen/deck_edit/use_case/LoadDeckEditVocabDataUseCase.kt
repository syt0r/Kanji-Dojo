package ua.syt0r.kanji.presentation.screen.main.screen.deck_edit.use_case

import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.RefreshableData
import ua.syt0r.kanji.core.VocabCardResolver
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.user_data.database.VocabCardData
import ua.syt0r.kanji.core.user_data.database.VocabPracticeRepository
import ua.syt0r.kanji.presentation.screen.main.screen.deck_edit.DeckEditItemAction
import ua.syt0r.kanji.presentation.screen.main.screen.deck_edit.DeckEditScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.deck_edit.VocabDeckEditListItem
import ua.syt0r.kanji.vocab_card_missing_meaning

interface LoadDeckEditVocabDataUseCase {

    suspend operator fun invoke(
        configuration: DeckEditScreenConfiguration.VocabDeck,
        defaultListItemAction: DeckEditItemAction,
        viewModelScope: CoroutineScope
    ): DeckEditVocabData

    fun handleMeaningLoading(item: VocabDeckEditListItem, scope: CoroutineScope)

}

data class DeckEditVocabData(
    val title: String?,
    val items: List<VocabDeckEditListItem>
)

class DefaultLoadDeckEditVocabDataUseCase(
    private val practiceRepository: VocabPracticeRepository,
    private val appDataRepository: AppDataRepository,
    private val vocabCardResolver: VocabCardResolver
) : LoadDeckEditVocabDataUseCase {

    override suspend operator fun invoke(
        configuration: DeckEditScreenConfiguration.VocabDeck,
        defaultListItemAction: DeckEditItemAction,
        viewModelScope: CoroutineScope
    ): DeckEditVocabData = withContext(Dispatchers.IO) {

        when (configuration) {

            is DeckEditScreenConfiguration.VocabDeck.CreateNew -> {
                DeckEditVocabData(null, emptyList())
            }

            is DeckEditScreenConfiguration.VocabDeck.CreateDerived -> {
                val classificationValue = configuration.classification.dbValue
                val words = appDataRepository.getImportDeckWords(classificationValue)

                DeckEditVocabData(
                    title = configuration.title,
                    items = words.mapIndexed { index, card ->
                        val cardData = VocabCardData(
                            kanjiReading = card.kanji,
                            kanaReading = card.kana,
                            meaning = card.meaning,
                            dictionaryId = card.id
                        )
                        VocabDeckEditListItem(
                            index = index,
                            cardData = cardData,
                            savedVocabCard = null,
                            initialAction = defaultListItemAction
                        ).apply { handleMeaningLoading(this, viewModelScope) }
                    }
                )
            }

            is DeckEditScreenConfiguration.VocabDeck.Edit -> {
                val deckCardIdList = practiceRepository.getCardIdList(configuration.vocabDeckId)
                val deckEditCards = deckCardIdList.mapIndexed { i, cardId ->
                    val card = vocabCardResolver.cardsCache.await().getValue(cardId)
                    VocabDeckEditListItem(
                        index = i,
                        cardData = card.data,
                        savedVocabCard = card,
                        initialAction = defaultListItemAction
                    ).apply { handleMeaningLoading(this, viewModelScope) }
                }

                DeckEditVocabData(
                    title = configuration.title,
                    items = deckEditCards
                )
            }
        }

    }

    override fun handleMeaningLoading(
        item: VocabDeckEditListItem,
        scope: CoroutineScope
    ) {
        item.meaning.subscriptionCount
            .map { it > 0 }
            .distinctUntilChanged()
            .flatMapLatest { hasSubs ->
                if (hasSubs) snapshotFlow { item.resultCardData.value }
                else flow { }
            }
            .map {
                it.meaning ?: it.dictionaryId
                    ?.let { appDataRepository.getDetailedWord(it) }
                    ?.let { it.senseList.firstOrNull()?.glossary?.firstOrNull() }
                ?: getString(Res.string.vocab_card_missing_meaning)
            }
            .flowOn(Dispatchers.IO)
            .onEach { item.meaning.value = RefreshableData.Loaded(it) }
            .launchIn(scope)
    }

}