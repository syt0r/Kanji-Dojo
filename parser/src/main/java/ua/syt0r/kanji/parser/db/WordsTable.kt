package ua.syt0r.kanji.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji.common.db.schema.WordTableSchema

object WordsTable : Table(WordTableSchema.name) {

    val id: Column<Long> = long(WordTableSchema.Columns.id).autoIncrement()
    val expression: Column<String> = text(WordTableSchema.Columns.expression)
    val furigana: Column<String> = text(WordTableSchema.Columns.furigana)
    val priority: Column<Int> = integer(WordTableSchema.Columns.priority)

    override val primaryKey = PrimaryKey(id)

}