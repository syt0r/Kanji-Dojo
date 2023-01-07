package ua.syt0r.kanji.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji.common.db.schema.KanjiStrokeTableSchema


object KanjiStrokesTable : Table(name = KanjiStrokeTableSchema.name) {

    val kanji: Column<String> = text(KanjiStrokeTableSchema.Columns.kanji)
    val strokeNumber: Column<Int> = integer(KanjiStrokeTableSchema.Columns.strokeNumber)
    val strokePath: Column<String> = text(KanjiStrokeTableSchema.Columns.strokePath)

    override val primaryKey = PrimaryKey(kanji, strokeNumber)

}