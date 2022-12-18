package ua.syt0r.kanji_dojo.parser

import com.google.gson.Gson
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ua.syt0r.kanji_dojo.parser.db.*
import ua.syt0r.kanji_dojo.parser.model.CharacterInfoData
import ua.syt0r.kanji_dojo.parser.model.CharacterStrokesData
import ua.syt0r.kanji_dojo.parser.model.Word
import ua.syt0r.kanji_dojo.shared.db.KanjiReadingTable

object DataExporter {

    fun writeKanjiStrokes(
        database: Database,
        list: List<CharacterStrokesData>
    ) = transaction(database) {

        SchemaUtils.create(KanjiStrokes)

        list.forEach { kanjiData ->
            kanjiData.strokes.forEachIndexed { index, path ->
                KanjiStrokes.insert {
                    it[kanji] = kanjiData.kanji.toString()
                    it[strokeNumber] = index
                    it[strokePath] = path
                }
            }
        }

    }

    fun writeKanjiData(
        database: Database,
        kanjiDataList: List<CharacterInfoData>
    ) = transaction(database) {

        SchemaUtils.create(KanjiMeanings)
        SchemaUtils.create(KanjiReadings)
        SchemaUtils.create(KanjiData)

        kanjiDataList.forEach { kanjiData ->

            kanjiData.meanings.forEachIndexed { priorityValue, meaningValue ->
                KanjiMeanings.insert {
                    it[kanji] = kanjiData.kanji.toString()
                    it[meaning] = meaningValue
                    it[priority] = priorityValue
                }
            }

            val readings = kanjiData.kunReadings.map { KanjiReadingTable.ReadingType.KUN to it } +
                    kanjiData.onReadings.map { KanjiReadingTable.ReadingType.ON to it }

            readings.forEach { (readingTypeEnum, readingStr) ->
                KanjiReadings.insert {
                    it[kanji] = kanjiData.kanji.toString()
                    it[reading] = readingStr
                    it[readingType] = readingTypeEnum.value
                }
            }

            KanjiData.insert {
                it[kanji] = kanjiData.kanji.toString()
                it[frequency] = kanjiData.frequency
                it[grade] = kanjiData.grade
                it[jlpt] = kanjiData.jlpt
            }
        }

    }

    fun writeWords(
        database: Database,
        words: List<Word>
    ) = transaction(database) {

        SchemaUtils.create(Words)
        SchemaUtils.create(WordMeanings)

        val gson = Gson()

        words.forEach { word ->

            val result = Words.insert {
                it[expression] = word.expression
                it[furigana] = gson.toJson(word.furigana)
                it[priority] = word.priority
            }

            val id = result[Words.id]

            word.meanings.forEachIndexed { index, meaningValue ->

                WordMeanings.insert {
                    it[expressionId] = id
                    it[meaning] = meaningValue
                    it[priority] = index
                }

            }

        }

    }

}