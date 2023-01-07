package ua.syt0r.kanji.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji.common.db.schema.WordMeaningTableSchema

object WordMeaningsTable : Table(WordMeaningTableSchema.name) {

    val expressionId: Column<Long> = long(WordMeaningTableSchema.Columns.expressionId)
    val meaning: Column<String> = text(WordMeaningTableSchema.Columns.meaning)
    val priority: Column<Int> = integer(WordMeaningTableSchema.Columns.priority)

    override val primaryKey = PrimaryKey(expressionId, meaning, priority)

}