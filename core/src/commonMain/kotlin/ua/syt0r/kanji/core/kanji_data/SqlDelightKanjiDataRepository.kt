package ua.syt0r.kanji.core.kanji_data

import kotlinx.coroutines.Deferred
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ua.syt0r.kanji.core.japanese.CharactersClassification
import ua.syt0r.kanji.core.kanji_data.data.CharacterRadical
import ua.syt0r.kanji.core.kanji_data.data.FuriganaDBEntity
import ua.syt0r.kanji.core.kanji_data.schema.KanjiReadingTableSchema
import ua.syt0r.kanji.core.kanji_data.data.*
import ua.syt0r.kanji.core.kanji_data.db.KanjiDatabase
import ua.syt0r.kanji.core.kanjidata.db.KanjiDataQueries

class SqlDelightKanjiDataRepository(
    private val deferredDatabase: Deferred<KanjiDatabase>
) : KanjiDataRepository {

    private suspend fun <T> runTransaction(
        transactionScope: KanjiDataQueries.() -> T
    ): T {
        val queries = deferredDatabase.await().kanjiDataQueries
        return queries.transactionWithResult { queries.transactionScope() }
    }

    override suspend fun getStrokes(kanji: String): List<String> = runTransaction {
        getStrokes(kanji).executeAsList()
    }

    override suspend fun getRadicalsInCharacter(
        character: String
    ): List<CharacterRadical> = runTransaction {
        getCharacterRadicals(character)
            .executeAsList()
            .map {
                it.run {
                    CharacterRadical(
                        character = character,
                        radical = radical,
                        startPosition = start_stroke.toInt(),
                        strokesCount = strokes_count.toInt()
                    )
                }
            }
    }

    override suspend fun getMeanings(kanji: String): List<String> = runTransaction {
        getMeanings(kanji).executeAsList()
    }

    override suspend fun getReadings(
        kanji: String
    ): Map<String, KanjiReadingTableSchema.ReadingType> = runTransaction {
        getReadings(kanji)
            .executeAsList()
            .associate { readingData ->
                readingData.reading to KanjiReadingTableSchema.ReadingType.values()
                    .find { it.value == readingData.reading_type }!!
            }
    }

    override suspend fun getData(kanji: String): KanjiData? = runTransaction {
        getData(kanji)
            .executeAsOneOrNull()
            ?.run { KanjiData(kanji, freq?.toInt()) }
    }

    override suspend fun getCharacterClassifications(
        kanji: String
    ): List<CharactersClassification> = runTransaction {
        getCharacterClassifications(kanji)
            .executeAsList()
            .map { CharactersClassification.fromString(it) }
    }

    override suspend fun getKanjiByClassification(
        classification: CharactersClassification
    ): List<String> = runTransaction {
        getCharactersByClassification(classification.toString())
            .executeAsList()
    }

    override suspend fun getWordsWithTextCount(text: String): Int = runTransaction {
        getDicEntryWithTextCount(text).executeAsOne().toInt()
    }

    override suspend fun getWordsWithText(
        text: String,
        offset: Int,
        limit: Int
    ): List<JapaneseWord> = runTransaction {
        getRankedDicEntryWithText(text, offset.toLong(), limit.toLong())
            .executeAsList()
            .map { dicEntryId ->
                val readings = getWordReadings(dicEntryId)
                    .executeAsList()
                    .map {
                        RankedReading(
                            it.expression,
                            it.kana_expression,
                            it.rank.toInt(),
                            it.furigana
                        )
                    }
                    .sortedWith(readingComparator(text))
                    .map { entity -> entity.toReading() }

                val meanings = getWordMeanings(dicEntryId)
                    .executeAsList()
                    .map { it.meaning }

                JapaneseWord(readings, meanings)
            }
    }

    override suspend fun getKanaWords(
        char: String,
        limit: Int
    ): List<JapaneseWord> = runTransaction {
        getKanaWordReadings("%$char%", limit.toLong())
            .executeAsList()
            .groupBy { it.dic_entry_id }
            .map { (id, readingEntities) ->
                val readings = readingEntities
                    .map {
                        RankedReading(
                            it.expression,
                            it.kana_expression,
                            it.rank.toInt(),
                            it.furigana
                        )
                    }
                    .sortedWith(readingComparator(char, true))
                    .map { it.toReading(kanaOnly = true) }
                    .distinct()

                val meanings = getWordMeanings(id)
                    .executeAsList()
                    .map { it.meaning }

                JapaneseWord(readings, meanings)
            }
    }

    override suspend fun getRadicals(): List<RadicalData> = runTransaction {
        getRadicals().executeAsList()
            .map { RadicalData(it.radical, it.strokesCount.toInt()) }
    }

    override suspend fun getCharactersWithRadicals(
        radicals: List<String>
    ): List<String> = runTransaction {
        getCharsWithRadicals(radicals, radicals.size.toLong())
            .executeAsList()
    }

    override suspend fun getAllRadicalsInCharactersWithSelectedRadicals(
        radicals: Set<String>
    ): List<String> = runTransaction {
        getAllRadicalsInCharactersWithSelectedRadicals(radicals, radicals.size.toLong())
            .executeAsList()
    }

    private data class RankedReading(
        val kanjiExpression: String?,
        val kanaExpression: String,
        val rank: Int,
        val furigana: String?
    )

    // To make sure that searched reading is the first in the list
    private fun readingComparator(
        prioritizedText: String,
        kanaOnly: Boolean = false
    ): Comparator<RankedReading> {
        return compareBy(
            {
                val containsText = it.kanjiExpression
                    ?.takeIf { !kanaOnly }
                    ?.contains(prioritizedText)
                    ?: it.kanaExpression.contains(prioritizedText)
                !containsText
            },
            { it.rank }
        )
    }

    private fun RankedReading.toReading(
        kanaOnly: Boolean = false
    ): FuriganaString {
        val compounds = furigana
            ?.takeIf { !kanaOnly }
            ?.let { Json.decodeFromString<List<FuriganaDBEntity>>(it) }
            ?.map { FuriganaStringCompound(it.text, it.annotation) }
            ?: listOf(FuriganaStringCompound(kanaExpression))
        return FuriganaString(compounds)
    }

}
