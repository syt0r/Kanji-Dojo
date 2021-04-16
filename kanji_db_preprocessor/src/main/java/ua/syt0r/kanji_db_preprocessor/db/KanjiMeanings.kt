package ua.syt0r.kanji_db_preprocessor.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ua.syt0r.kanji_db_model.db.MeaningsTableConstants


object KanjiMeanings : Table(name = MeaningsTableConstants.TABLE_NAME) {

    val kanji: Column<String> = text(MeaningsTableConstants.KANJI_COLUMN)
    val meaning: Column<String> = text(MeaningsTableConstants.MEANING_TYPE_COLUMN)

    override val primaryKey = PrimaryKey(kanji, meaning)

}