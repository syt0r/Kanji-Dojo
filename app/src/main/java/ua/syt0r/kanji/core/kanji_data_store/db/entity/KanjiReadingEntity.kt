package ua.syt0r.kanji.core.kanji_data_store.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji_db_model.db.KanjiReadingTable.KANJI_COLUMN
import ua.syt0r.kanji_db_model.db.KanjiReadingTable.READING_COLUMN
import ua.syt0r.kanji_db_model.db.KanjiReadingTable.READING_TYPE_COLUMN
import ua.syt0r.kanji_db_model.db.KanjiReadingTable.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = [KANJI_COLUMN, READING_TYPE_COLUMN, READING_COLUMN],
)
data class KanjiReadingEntity(
    @ColumnInfo(name = KANJI_COLUMN) val kanji: String,
    @ColumnInfo(name = READING_COLUMN) val reading: String,
    @ColumnInfo(name = READING_TYPE_COLUMN) val type: String
)