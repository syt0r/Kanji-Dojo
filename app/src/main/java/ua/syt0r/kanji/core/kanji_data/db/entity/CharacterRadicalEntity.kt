package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji.common.db.schema.CharacterRadicalTableSchema
import ua.syt0r.kanji.common.db.schema.CharacterRadicalTableSchema.Columns

@Entity(
    tableName = CharacterRadicalTableSchema.name,
    primaryKeys = [Columns.character, Columns.radical, Columns.startStrokeIndex]
)
class CharacterRadicalEntity(
    @ColumnInfo(name = Columns.character) val character: String,
    @ColumnInfo(name = Columns.radical) val radical: String,
    @ColumnInfo(name = Columns.startStrokeIndex) val startStrokeIndex: Int,
    @ColumnInfo(name = Columns.strokesCount) val strokesCount: Int
)