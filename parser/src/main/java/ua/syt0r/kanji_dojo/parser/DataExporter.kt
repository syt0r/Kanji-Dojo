package ua.syt0r.kanji_dojo.parser

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ua.syt0r.kanji_dojo.parser.db.KanjiData
import ua.syt0r.kanji_dojo.parser.db.KanjiMeanings
import ua.syt0r.kanji_dojo.parser.db.KanjiReadings
import ua.syt0r.kanji_dojo.parser.db.KanjiStrokes
import ua.syt0r.kanji_dojo.parser.model.KanjiInfoData
import ua.syt0r.kanji_dojo.parser.model.KanjiStrokesData
import ua.syt0r.kanji_dojo.shared.db.KanjiReadingTable

object DataExporter {

    fun writeKanjiStrokes(
        database: Database,
        list: List<KanjiStrokesData>
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
        kanjiDataList: List<KanjiInfoData>
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

}