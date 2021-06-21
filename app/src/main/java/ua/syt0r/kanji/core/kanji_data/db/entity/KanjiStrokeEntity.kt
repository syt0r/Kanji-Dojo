package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji_db_model.db.KanjiStrokeTable
import ua.syt0r.kanji_db_model.db.KanjiStrokeTable.KANJI_COLUMN
import ua.syt0r.kanji_db_model.db.KanjiStrokeTable.STROKE_NUMBER_COLUMN
import ua.syt0r.kanji_db_model.db.KanjiStrokeTable.STROKE_PATH_COLUMN

@Entity(
    tableName = KanjiStrokeTable.TABLE_NAME,
    primaryKeys = [KANJI_COLUMN, STROKE_NUMBER_COLUMN],
)
data class KanjiStrokeEntity(
    @ColumnInfo(name = KANJI_COLUMN) val kanji: String,
    @ColumnInfo(name = STROKE_NUMBER_COLUMN) val strokeNumber: Int,
    @ColumnInfo(name = STROKE_PATH_COLUMN) val strokePath: String
)