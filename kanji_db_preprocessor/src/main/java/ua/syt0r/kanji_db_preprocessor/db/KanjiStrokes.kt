package ua.syt0r.kanji_db_preprocessor.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_db_model.db.KanjiStrokeTable


object KanjiStrokes : Table(name = KanjiStrokeTable.TABLE_NAME) {

    val kanji: Column<String> = text(KanjiStrokeTable.KANJI_COLUMN)
    val strokeNumber: Column<Int> = integer(KanjiStrokeTable.STROKE_NUMBER_COLUMN)
    val strokePath: Column<String> = text(KanjiStrokeTable.STROKE_PATH_COLUMN)

    override val primaryKey = PrimaryKey(kanji, strokeNumber)

}