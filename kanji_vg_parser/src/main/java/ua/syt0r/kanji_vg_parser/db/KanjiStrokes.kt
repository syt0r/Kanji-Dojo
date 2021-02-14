package ua.syt0r.kanji_vg_parser.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_model.db.StrokesTableConstants


object KanjiStrokes : Table(name = StrokesTableConstants.TABLE_NAME) {

    val kanji: Column<String> = text(StrokesTableConstants.KANJI_COLUMN)
    val strokeNumber: Column<Int> = integer(StrokesTableConstants.STROKE_NUMBER_COLUMN)
    val strokePath: Column<String> = text(StrokesTableConstants.STROKE_PATH_COLUMN)

    override val primaryKey = PrimaryKey(kanji, strokeNumber)

}