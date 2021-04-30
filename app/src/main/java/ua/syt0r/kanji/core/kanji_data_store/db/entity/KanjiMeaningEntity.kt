package ua.syt0r.kanji.core.kanji_data_store.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji_db_model.db.KanjiMeaningTable.KANJI_COLUMN
import ua.syt0r.kanji_db_model.db.KanjiMeaningTable.MEANING_COLUMN
import ua.syt0r.kanji_db_model.db.KanjiMeaningTable.PRIORITY_COLUMN
import ua.syt0r.kanji_db_model.db.KanjiMeaningTable.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = [KANJI_COLUMN, MEANING_COLUMN, PRIORITY_COLUMN],
)
data class KanjiMeaningEntity(
    @ColumnInfo(name = KANJI_COLUMN) val kanji: String,
    @ColumnInfo(name = MEANING_COLUMN) val meaning: String,
    @ColumnInfo(name = PRIORITY_COLUMN) val priority: Int
)