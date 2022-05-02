package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji_dojo.shared.db.KanjiStrokeTable

@Entity(
    tableName = KanjiStrokeTable.name,
    primaryKeys = [KanjiStrokeTable.Columns.kanji, KanjiStrokeTable.Columns.strokeNumber],
)
data class KanjiStrokeEntity(
    @ColumnInfo(name = KanjiStrokeTable.Columns.kanji) val kanji: String,
    @ColumnInfo(name = KanjiStrokeTable.Columns.strokeNumber) val strokeNumber: Int,
    @ColumnInfo(name = KanjiStrokeTable.Columns.strokePath) val strokePath: String
)