package ua.syt0r.kanji.parser

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import ua.syt0r.kanji.common.db.entity.CharacterRadical
import ua.syt0r.kanji.common.db.entity.Radical
import ua.syt0r.kanji.common.db.entity.asJsonString
import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema.ReadingType
import ua.syt0r.kanji.parser.db.*
import ua.syt0r.kanji.parser.model.CharacterClass
import ua.syt0r.kanji.parser.model.CharacterInfoData
import ua.syt0r.kanji.parser.model.Expression
import ua.syt0r.kanji.parser.model.Word

class DataExporter(
    private val database: Database
) {

    fun writeKanjiStrokes(characterToStrokes: Map<String, List<String>>) = transaction(database) {
        SchemaUtils.create(KanjiStrokesTable)

        characterToStrokes.forEach { (character, strokes) ->
            strokes.forEachIndexed { index, path ->
                KanjiStrokesTable.insert {
                    it[kanji] = character
                    it[strokeNumber] = index
                    it[strokePath] = path
                }
            }
        }
    }

    fun writeKanjiData(kanjiDataList: List<CharacterInfoData>) = transaction(database) {
        SchemaUtils.create(KanjiMeaningsTable)
        SchemaUtils.create(KanjiReadingsTable)
        SchemaUtils.create(KanjiDataTable)

        kanjiDataList.forEach { kanjiData ->

            kanjiData.meanings.forEachIndexed { priorityValue, meaningValue ->
                KanjiMeaningsTable.insert {
                    it[kanji] = kanjiData.kanji.toString()
                    it[meaning] = meaningValue
                    it[priority] = priorityValue
                }
            }

            val readings = kanjiData.kunReadings.map { ReadingType.KUN to it } +
                    kanjiData.onReadings.map { ReadingType.ON to it }

            readings.forEach { (readingTypeEnum, readingStr) ->
                KanjiReadingsTable.insert {
                    it[kanji] = kanjiData.kanji.toString()
                    it[reading] = readingStr
                    it[readingType] = readingTypeEnum.value
                }
            }

            KanjiDataTable.insert {
                it[kanji] = kanjiData.kanji.toString()
                it[frequency] = kanjiData.frequency
            }
        }
    }

    fun writeWords(words: List<Word>) = transaction(database) {
        SchemaUtils.create(DictionaryEntryTable)
        SchemaUtils.create(DictionaryReadingsTable)
        SchemaUtils.create(DictionaryMeaningsTable)

        words.forEach { word ->

            val result = DictionaryEntryTable.insert {}
            val wordId = result[DictionaryEntryTable.id]

            word.expressions.forEach { expression ->
                DictionaryReadingsTable.insert {

                    it[dictionaryEntryId] = wordId
                    it[rank] = expression.rank

                    when (expression) {
                        is Expression.KanjiExpression -> {
                            it[kanjiExpression] = expression.expression
                            it[kanaExpression] = expression.reading
                            it[furigana] = expression.furigana.asJsonString()
                        }
                        is Expression.KanaExpression -> {
                            it[dictionaryEntryId] = wordId
                            it[kanaExpression] = expression.reading
                        }
                    }

                }
            }

            word.meanings.forEachIndexed { index, meaningValue ->

                DictionaryMeaningsTable.insert {
                    it[dictionaryEntryIdId] = wordId
                    it[meaning] = meaningValue
                    it[priority] = index
                }

            }

        }
    }

    fun writeRadicals(radicals: List<Radical>) = transaction(database) {
        SchemaUtils.create(RadicalTable)

        radicals.forEach { rad ->
            RadicalTable.insert {
                it[radical] = rad.radical
                it[strokes] = rad.strokesCount
            }
        }
    }

    fun writeCharacterRadicals(data: List<CharacterRadical>) = transaction(database) {
        SchemaUtils.create(CharacterRadicalTable)

        data.forEach { item ->
            CharacterRadicalTable.insertIgnore {
                it[character] = item.character
                it[radical] = item.radical
                it[startStrokeIndex] = item.startPosition
                it[strokes] = item.strokesCount
            }
        }
    }

    fun writeClassifications(data: List<CharacterClass>) = transaction(database) {
        SchemaUtils.create(KanjiClassificationTable)

        data.forEach { item ->
            KanjiClassificationTable.insert {
                it[kanji] = item.char
                it[classification] = item.clazz.toString()
            }
        }
    }

}