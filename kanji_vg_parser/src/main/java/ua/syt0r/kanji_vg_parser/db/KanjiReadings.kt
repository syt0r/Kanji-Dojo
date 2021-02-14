package ua.syt0r.kanji_vg_parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_model.db.ReadingsTableConstants

object KanjiReadings : Table(name = ReadingsTableConstants.TABLE_NAME) {
    val kanji: Column<String> = text(ReadingsTableConstants.KANJI_COLUMN)
    val readingType: Column<String> = text(ReadingsTableConstants.READING_TYPE_COLUMN)
    val reading: Column<String> = text(ReadingsTableConstants.READING_COLUMN)

    override val primaryKey = PrimaryKey(kanji, readingType, reading)
}