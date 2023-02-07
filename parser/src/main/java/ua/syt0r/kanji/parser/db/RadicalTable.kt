package ua.syt0r.kanji.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji.common.db.schema.RadicalTableSchema

object RadicalTable : Table(name = RadicalTableSchema.name) {

    val radical: Column<String> = text(RadicalTableSchema.Columns.radical)
    val strokes: Column<Int> = integer(RadicalTableSchema.Columns.strokesCount)

    override val primaryKey = PrimaryKey(radical, strokes)

}