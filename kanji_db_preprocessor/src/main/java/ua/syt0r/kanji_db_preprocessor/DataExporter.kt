package ua.syt0r.kanji_db_preprocessor

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ua.syt0r.kanji_db_model.db.KanjiReadingTable
import ua.syt0r.kanji_db_preprocessor.db.KanjiClassification
import ua.syt0r.kanji_db_preprocessor.db.KanjiMeanings
import ua.syt0r.kanji_db_preprocessor.db.KanjiReadings
import ua.syt0r.kanji_db_preprocessor.db.KanjiStrokes
import ua.syt0r.kanji_db_preprocessor.parsers.KanjiDictData
import ua.syt0r.kanji_db_preprocessor.parsers.KanjiVGData

object DataExporter {

    fun writeKanjiStrokes(
        database: Database,
        kanjiVGDataList: List<KanjiVGData>
    ) = transaction(database) {

        SchemaUtils.create(KanjiStrokes)

        kanjiVGDataList.forEach { kanjiData ->
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
        kanjiDataList: List<KanjiDictData>
    ) = transaction(database) {

        SchemaUtils.create(KanjiMeanings)
        SchemaUtils.create(KanjiReadings)
        SchemaUtils.create(KanjiClassification)

        kanjiDataList.forEach { kanjiData ->

            val meanings = kanjiData.compactMeaning.map { it to 0 } +
                    kanjiData.meaning.map { it to 1 }

            meanings.forEach { (meaningValue, priorityValue) ->
                KanjiMeanings.insert {
                    it[kanji] = kanjiData.kanji.toString()
                    it[meaning] = meaningValue
                    it[priority] = priorityValue
                }
            }

            val readings = kanjiData.kunyomi.map { KanjiReadingTable.ReadingType.KUN to it } +
                    kanjiData.onyomi.map { KanjiReadingTable.ReadingType.ON to it }

            readings.forEach { (readingTypeEnum, readingStr) ->
                KanjiReadings.insert {
                    it[kanji] = kanjiData.kanji.toString()
                    it[reading] = readingStr
                    it[readingType] = readingTypeEnum.value
                }
            }

            KanjiClassification.insert {
                it[kanji] = kanjiData.kanji.toString()
                it[className] = kanjiData.grade
            }

            KanjiClassification.insert {
                it[kanji] = kanjiData.kanji.toString()
                it[className] = kanjiData.jlpt
            }

        }

    }

}