package ua.syt0r.kanji_dojo.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_dojo.shared.db.WordMeaningTable

object WordMeanings : Table(WordMeaningTable.name) {

    val expressionId: Column<Long> = long(WordMeaningTable.Columns.expressionId)
    val meaning: Column<String> = text(WordMeaningTable.Columns.meaning)
    val priority: Column<Int> = integer(WordMeaningTable.Columns.priority)

    override val primaryKey = PrimaryKey(expressionId, meaning, priority)

}