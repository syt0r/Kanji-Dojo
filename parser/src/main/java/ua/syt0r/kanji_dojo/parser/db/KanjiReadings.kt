package ua.syt0r.kanji_dojo.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_dojo.shared.db.KanjiReadingTable

object KanjiReadings : Table(name = KanjiReadingTable.name) {

    val kanji: Column<String> = text(KanjiReadingTable.Columns.kanji)
    val readingType: Column<String> = text(KanjiReadingTable.Columns.readingType)
    val reading: Column<String> = text(KanjiReadingTable.Columns.reading)

    override val primaryKey = PrimaryKey(kanji, readingType, reading)

}