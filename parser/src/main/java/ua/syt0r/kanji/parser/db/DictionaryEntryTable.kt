package ua.syt0r.kanji.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji.common.db.schema.DictionaryEntryTableSchema

object DictionaryEntryTable : Table(DictionaryEntryTableSchema.name) {

    val id: Column<Long> = long(DictionaryEntryTableSchema.Columns.id).autoIncrement()

    override val primaryKey = PrimaryKey(id)

}