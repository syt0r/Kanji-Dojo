package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji.common.db.schema.KanjiMeaningTableSchema

@Entity(
    tableName = KanjiMeaningTableSchema.name,
    primaryKeys = [KanjiMeaningTableSchema.Columns.kanji, KanjiMeaningTableSchema.Columns.meaning, KanjiMeaningTableSchema.Columns.priority],
)
data class KanjiMeaningEntity(
    @ColumnInfo(name = KanjiMeaningTableSchema.Columns.kanji) val kanji: String,
    @ColumnInfo(name = KanjiMeaningTableSchema.Columns.meaning) val meaning: String,
    @ColumnInfo(name = KanjiMeaningTableSchema.Columns.priority) val priority: Int
)