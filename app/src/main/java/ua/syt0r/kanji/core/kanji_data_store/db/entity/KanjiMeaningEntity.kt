package ua.syt0r.kanji.core.kanji_data_store.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji_db_model.db.MeaningsTableConstants

@Entity(
    tableName = MeaningsTableConstants.TABLE_NAME,
    primaryKeys = [MeaningsTableConstants.KANJI_COLUMN, MeaningsTableConstants.MEANING_TYPE_COLUMN]
)
data class KanjiMeaningEntity(
    @ColumnInfo(name = MeaningsTableConstants.KANJI_COLUMN) val kanji: String,
    @ColumnInfo(name = MeaningsTableConstants.MEANING_TYPE_COLUMN) val meaning: String
)