package ua.syt0r.kanji_dojo.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_dojo.shared.db.WordTable

object Words : Table(WordTable.name) {

    val id: Column<Long> = long(WordTable.Columns.id).autoIncrement()
    val expression: Column<String> = text(WordTable.Columns.expression)
    val furigana: Column<String> = text(WordTable.Columns.furigana)
    val priority: Column<Int> = integer(WordTable.Columns.priority)

    override val primaryKey = PrimaryKey(id)

}