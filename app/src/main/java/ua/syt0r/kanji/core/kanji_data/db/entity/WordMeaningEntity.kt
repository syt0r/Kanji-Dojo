package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji.common.db.schema.WordMeaningTableSchema

@Entity(
    tableName = WordMeaningTableSchema.name,
    primaryKeys = [WordMeaningTableSchema.Columns.expressionId, WordMeaningTableSchema.Columns.meaning, WordMeaningTableSchema.Columns.priority]
)
data class WordMeaningEntity(
    @ColumnInfo(name = WordMeaningTableSchema.Columns.expressionId) val expressionId: Long,
    @ColumnInfo(name = WordMeaningTableSchema.Columns.meaning) val meaning: String,
    @ColumnInfo(name = WordMeaningTableSchema.Columns.priority) val priority: Int
)