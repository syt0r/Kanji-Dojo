package ua.syt0r.kanji_db_preprocessor.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_db_model.db.KanjiReadingTable

object KanjiReadings : Table(name = KanjiReadingTable.TABLE_NAME) {

    val kanji: Column<String> = text(KanjiReadingTable.KANJI_COLUMN)
    val readingType: Column<String> = text(KanjiReadingTable.READING_TYPE_COLUMN)
    val reading: Column<String> = text(KanjiReadingTable.READING_COLUMN)

    override val primaryKey = PrimaryKey(kanji, readingType, reading)

}