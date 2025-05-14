package ua.syt0r.kanji.presentation.screen.main.screen.deck_edit.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import ua.syt0r.kanji.Res
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
        defaultListItemAction: DeckEditItemAction
    ): DeckEditVocabData

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
        defaultListItemAction: DeckEditItemAction
    ): DeckEditVocabData = withContext(Dispatchers.IO) {

        when (configuration) {

            is DeckEditScreenConfiguration.VocabDeck.CreateNew -> {
                DeckEditVocabData(null, emptyList())
            }

            is DeckEditScreenConfiguration.VocabDeck.CreateDerived -> {
                val classificationValue = configuration.classification.dbValue
                val words = appDataRepository.getImportDeckWords(classificationValue)

                val noMeaningWordIdSet = words.filter { it.meaning == null }.map { it.id }.toSet()
                val senseList = appDataRepository
                    .getWordSenses(noMeaningWordIdSet)
                    .associateBy { it.wordId }

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
                            fallbackMeaning = senseList[card.id]?.senseList?.asSequence()
                                ?.let { it.flatMap { it.glossary }.firstOrNull() }
                                ?: getString(Res.string.vocab_card_missing_meaning),
                            initialAction = defaultListItemAction
                        )
                    }
                )
            }

            is DeckEditScreenConfiguration.VocabDeck.Edit -> {
                val deckCardIdList = practiceRepository.getCardIdList(configuration.vocabDeckId)
                val deckEditCards = deckCardIdList.mapIndexed { i, cardId ->
                    val resolvedCard = vocabCardResolver.resolveUserCard(cardId)
                    VocabDeckEditListItem(
                        index = i,
                        cardData = resolvedCard.card.data,
                        savedVocabCard = resolvedCard.card,
                        fallbackMeaning = resolvedCard.meaning,
                        initialAction = defaultListItemAction
                    )
                }

                DeckEditVocabData(
                    title = configuration.title,
                    items = deckEditCards
                )
            }
        }

    }

}