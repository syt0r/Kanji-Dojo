package ua.syt0r.kanji_db_preprocessor.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_db_model.db.KanjiMeaningTable


object KanjiMeanings : Table(name = KanjiMeaningTable.TABLE_NAME) {

    val kanji: Column<String> = text(KanjiMeaningTable.KANJI_COLUMN)
    val meaning: Column<String> = text(KanjiMeaningTable.MEANING_COLUMN)
    val priority: Column<Int> = integer(KanjiMeaningTable.PRIORITY_COLUMN)

    override val primaryKey = PrimaryKey(kanji, meaning, priority)

}