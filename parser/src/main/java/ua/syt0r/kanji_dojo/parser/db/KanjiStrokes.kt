package ua.syt0r.kanji_dojo.parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_dojo.shared.db.KanjiStrokeTable


object KanjiStrokes : Table(name = KanjiStrokeTable.name) {

    val kanji: Column<String> = text(KanjiStrokeTable.Columns.kanji)
    val strokeNumber: Column<Int> = integer(KanjiStrokeTable.Columns.strokeNumber)
    val strokePath: Column<String> = text(KanjiStrokeTable.Columns.strokePath)

    override val primaryKey = PrimaryKey(kanji, strokeNumber)

}