package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji.common.db.schema.RadicalTableSchema
import ua.syt0r.kanji.common.db.schema.RadicalTableSchema.Columns

@Entity(
    tableName = RadicalTableSchema.name,
    primaryKeys = [Columns.radical, Columns.strokesCount]
)
class RadicalEntity(
    @ColumnInfo(name = Columns.radical) val radical: String,
    @ColumnInfo(name = Columns.strokesCount) val strokesCount: Int
)