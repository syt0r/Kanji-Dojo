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

data class ResolvedVocabDictionaryData(
    val furigana: FuriganaString?,
    val meaning: String?,
    val pos: List<String>?
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

        val dicData = vocabCard.data.dictionaryId
            ?.let { resolveDictionaryData(it, kanjiReading, kanaReading) }

        return ResolvedVocabCard(
            dictionaryId = vocabCard.data.dictionaryId,
            kanjiReading = kanjiReading,
            kanaReading = kanaReading,
            furigana = dicData?.furigana,
            meaning = dicData?.meaning ?: getString(Res.string.vocab_card_missing_meaning),
            pos = dicData?.pos ?: emptyList(),
            card = vocabCard
        )
    }

    private suspend fun resolveDictionaryData(
        jmDictId: Long,
        kanjiReading: String?,
        kanaReading: String
    ): ResolvedVocabDictionaryData {
        val word = appDataRepository.getWord(jmDictId, kanjiReading, kanaReading)
        if (word != null) {
            return ResolvedVocabDictionaryData(
                furigana = word.reading.furigana,
                meaning = word.combinedGlossary(),
                pos = word.partOfSpeechList
            )
        }

        val detailedWord = appDataRepository.getDetailedWord(jmDictId)
        if (detailedWord != null) {
            return ResolvedVocabDictionaryData(
                furigana = null,
                meaning = detailedWord.senseList.asSequence().flatMap { it.glossary }.firstOrNull(),
                pos = detailedWord.senseList.asSequence().flatMap { it.partOfSpeechList }.toList()
            )
        }

        return ResolvedVocabDictionaryData(
            furigana = null,
            meaning = null,
            pos = null
        )
    }

}