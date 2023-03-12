package ua.syt0r.kanji.core.kanji_data

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.common.db.entity.CharacterRadical
import ua.syt0r.kanji.common.db.entity.FuriganaDBEntity
import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema
import ua.syt0r.kanji.core.kanji_data.data.*
import ua.syt0r.kanji.core.kanjidata.db.KanjiDataQueries

class SqlDelightKanjiDataRepository(
    private val kanjiDataQueries: KanjiDataQueries
) : KanjiDataRepository {

    override fun getStrokes(kanji: String): List<String> {
        return kanjiDataQueries.getStrokes(kanji).executeAsList()
    }

    override fun getRadicalsInCharacter(character: String): List<CharacterRadical> {
        return kanjiDataQueries.getCharacterRadicals(character)
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

    override fun getMeanings(kanji: String): List<String> {
        return kanjiDataQueries.getMeanings(kanji).executeAsList()
    }

    override fun getReadings(kanji: String): Map<String, KanjiReadingTableSchema.ReadingType> {
        return kanjiDataQueries.getReadings(kanji)
            .executeAsList()
            .associate { readingData ->
                readingData.reading to KanjiReadingTableSchema.ReadingType.values()
                    .find { it.value == readingData.reading_type }!!
            }
    }

    override fun getData(kanji: String): KanjiData? {
        return kanjiDataQueries.getData(kanji)
            .executeAsOneOrNull()
            ?.run { KanjiData(kanji, freq?.toInt()) }
    }

    override fun getCharacterClassifications(kanji: String): List<CharactersClassification> {
        return kanjiDataQueries.getCharacterClassifications(kanji)
            .executeAsList()
            .map { CharactersClassification.fromString(it) }
    }

    override fun getKanjiByClassification(classification: CharactersClassification): List<String> {
        return kanjiDataQueries.getCharactersByClassification(classification.toString())
            .executeAsList()
    }

    override fun getWordsWithText(text: String, limit: Int): List<JapaneseWord> {
        return kanjiDataQueries.getRankedDicEntryWithText(text, limit.toLong())
            .executeAsList()
            .map { dicEntryId ->
                val readings = kanjiDataQueries.getWordReadings(dicEntryId)
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

                val meanings = kanjiDataQueries.getWordMeanings(dicEntryId)
                    .executeAsList()
                    .map { it.meaning }

                JapaneseWord(readings, meanings)
            }
    }

    override fun getKanaWords(char: String, limit: Int): List<JapaneseWord> {
        return kanjiDataQueries.getKanaWordReadings("%$char%", limit.toLong())
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

                val meanings = kanjiDataQueries.getWordMeanings(id)
                    .executeAsList()
                    .map { it.meaning }

                JapaneseWord(readings, meanings)
            }
    }

    override fun getRadicals(): List<RadicalData> {
        return kanjiDataQueries.getRadicals().executeAsList()
            .map { RadicalData(it.radical, it.strokesCount.toInt()) }
    }

    override fun getCharactersWithRadicals(radicals: List<String>): List<String> {
        return kanjiDataQueries.getCharsWithRadicals(radicals, radicals.size.toLong())
            .executeAsList()
    }

    override fun getAllRadicalsInCharacters(characters: List<String>): List<String> {
        return kanjiDataQueries.getAllRadicalsInCharacters(characters).executeAsList()
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
