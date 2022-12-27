package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji.common.db.WordMeaningTable

@Entity(
    tableName = WordMeaningTable.name,
    primaryKeys = [WordMeaningTable.Columns.expressionId, WordMeaningTable.Columns.meaning, WordMeaningTable.Columns.priority]
)
data class WordMeaningEntity(
    @ColumnInfo(name = WordMeaningTable.Columns.expressionId) val expressionId: Long,
    @ColumnInfo(name = WordMeaningTable.Columns.meaning) val meaning: String,
    @ColumnInfo(name = WordMeaningTable.Columns.priority) val priority: Int
)