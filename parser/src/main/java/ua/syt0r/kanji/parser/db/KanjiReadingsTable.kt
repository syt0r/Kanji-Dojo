package ua.syt0r.kanji.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema

object KanjiReadingsTable : Table(name = KanjiReadingTableSchema.name) {

    val kanji: Column<String> = text(KanjiReadingTableSchema.Columns.kanji)
    val readingType: Column<String> = text(KanjiReadingTableSchema.Columns.readingType)
    val reading: Column<String> = text(KanjiReadingTableSchema.Columns.reading)

    override val primaryKey = PrimaryKey(kanji, readingType, reading)

}