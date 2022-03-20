package ua.syt0r.kanji_db_preprocessor.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_db_model.db.KanjiClassificationTable

object KanjiClassification : Table(name = KanjiClassificationTable.TABLE_NAME) {

    val kanji: Column<String> = text(KanjiClassificationTable.KANJI_COLUMN)
    val className: Column<String> = text(KanjiClassificationTable.CLASS_COLUMN)

    override val primaryKey = PrimaryKey(kanji, className)

}