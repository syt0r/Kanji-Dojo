package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji.common.db.schema.KanjiStrokeTableSchema

@Entity(
    tableName = KanjiStrokeTableSchema.name,
    primaryKeys = [KanjiStrokeTableSchema.Columns.kanji, KanjiStrokeTableSchema.Columns.strokeNumber],
)
data class KanjiStrokeEntity(
    @ColumnInfo(name = KanjiStrokeTableSchema.Columns.kanji) val kanji: String,
    @ColumnInfo(name = KanjiStrokeTableSchema.Columns.strokeNumber) val strokeNumber: Int,
    @ColumnInfo(name = KanjiStrokeTableSchema.Columns.strokePath) val strokePath: String
)