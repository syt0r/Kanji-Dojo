package ua.syt0r.kanji.core

import org.jetbrains.compose.resources.getString
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.app_data.data.FuriganaString
import ua.syt0r.kanji.core.user_data.database.CachedUserDataState
import ua.syt0r.kanji.core.user_data.database.SavedVocabCard
import ua.syt0r.kanji.core.user_data.database.VocabPracticeRepository
import ua.syt0r.kanji.presentation.screen.main.screen.info.InfoScreenData
import ua.syt0r.kanji.vocab_card_missing_meaning

data class ResolvedVocabCard(
    val dictionaryId: Long?,
    val kanjiReading: String?,
    val kanaReading: String,
    val furigana: FuriganaString?,
    val meaning: String,
    val pos: List<String>,
    val card: SavedVocabCard
)

fun ResolvedVocabCard.toInfoScreenData() =
    InfoScreenData.Vocab(dictionaryId, kanjiReading, kanaReading)

class VocabCardResolver(
    private val vocabPracticeRepository: VocabPracticeRepository,
    private val appDataRepository: AppDataRepository
) {

    private val cardsCache = CachedUserDataState(
        resetFlow = vocabPracticeRepository.changesFlow,
        provider = { vocabPracticeRepository.getAllCards().associateBy { it.cardId } },
        debugTitle = "vocab_cards"
    )

    suspend fun resolveUserCard(cardId: Long): ResolvedVocabCard {
        val cards = cardsCache.await()
        val vocabCard = cards.getValue(cardId)

        val kanjiReading = vocabCard.data.kanjiReading
        val kanaReading = vocabCard.data.kanaReading

        val jmDictWord = vocabCard.data.dictionaryId
            ?.let { appDataRepository.getWord(it, kanjiReading, kanaReading) }

        return ResolvedVocabCard(
            dictionaryId = vocabCard.data.dictionaryId,
            kanjiReading = kanjiReading,
            kanaReading = kanaReading,
            furigana = jmDictWord?.reading?.furigana,
            meaning = vocabCard.data.meaning
                ?: jmDictWord?.combinedGlossary()
                ?: getString(Res.string.vocab_card_missing_meaning),
            pos = jmDictWord?.partOfSpeechList ?: emptyList(),
            card = vocabCard
        )
    }

    suspend fun resolveAllUserCard(cardIdList: List<Long>): List<ResolvedVocabCard> {
        val cards = cardsCache.await()
        val vocabCards = cardIdList.map { cards.getValue(it) }
        val jmDictWordsForTranslationsMap = vocabCards
            .mapNotNull {
                it.data.run {
                    meaning ?: return@mapNotNull null
                    appDataRepository.getWord(
                        id = dictionaryId ?: return@mapNotNull null,
                        kanjiReading = kanjiReading,
                        kanaReading = kanaReading
                    )
                }
            }
            .associateBy { it.id }


        return vocabCards.map { vocabCard ->
            val kanjiReading = vocabCard.data.kanjiReading
            val kanaReading = vocabCard.data.kanaReading
            val jmDictWord = jmDictWordsForTranslationsMap[vocabCard.data.dictionaryId]

            ResolvedVocabCard(
                dictionaryId = vocabCard.data.dictionaryId,
                kanjiReading = kanjiReading,
                kanaReading = kanaReading,
                furigana = jmDictWord?.reading?.furigana,
                meaning = vocabCard.data.meaning
                    ?: jmDictWord?.combinedGlossary()
                    ?: getString(Res.string.vocab_card_missing_meaning),
                pos = jmDictWord?.partOfSpeechList ?: emptyList(),
                card = vocabCard
            )
        }
    }

}