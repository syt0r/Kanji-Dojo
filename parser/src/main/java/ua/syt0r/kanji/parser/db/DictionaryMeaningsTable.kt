package ua.syt0r.kanji.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji.common.db.schema.DictionaryMeaningTableSchema

object DictionaryMeaningsTable : Table(DictionaryMeaningTableSchema.name) {

    val dictionaryEntryIdId: Column<Long> = long(DictionaryMeaningTableSchema.Columns.expressionId)
    val meaning: Column<String> = text(DictionaryMeaningTableSchema.Columns.meaning)
    val priority: Column<Int> = integer(DictionaryMeaningTableSchema.Columns.priority)

    override val primaryKey = PrimaryKey(dictionaryEntryIdId, meaning, priority)

}