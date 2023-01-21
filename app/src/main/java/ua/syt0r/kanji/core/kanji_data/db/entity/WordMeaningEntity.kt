package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji.common.db.schema.DictionaryMeaningTableSchema
import ua.syt0r.kanji.common.db.schema.DictionaryMeaningTableSchema.Columns

@Entity(
    tableName = DictionaryMeaningTableSchema.name,
    primaryKeys = [Columns.expressionId, Columns.meaning, Columns.priority]
)
data class WordMeaningEntity(
    @ColumnInfo(name = Columns.expressionId) val expressionId: Long,
    @ColumnInfo(name = Columns.meaning) val meaning: String,
    @ColumnInfo(name = Columns.priority) val priority: Int
)