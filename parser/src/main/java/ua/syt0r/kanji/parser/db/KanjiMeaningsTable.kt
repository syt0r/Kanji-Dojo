package ua.syt0r.kanji.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji.common.db.schema.KanjiMeaningTableSchema


object KanjiMeaningsTable : Table(name = KanjiMeaningTableSchema.name) {

    val kanji: Column<String> = text(KanjiMeaningTableSchema.Columns.kanji)
    val meaning: Column<String> = text(KanjiMeaningTableSchema.Columns.meaning)
    val priority: Column<Int> = integer(KanjiMeaningTableSchema.Columns.priority)

    override val primaryKey = PrimaryKey(kanji, meaning, priority)

}