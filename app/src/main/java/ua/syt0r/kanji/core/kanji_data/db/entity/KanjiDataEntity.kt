package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ua.syt0r.kanji.common.db.schema.KanjiDataTableSchema

@Entity(
    tableName = KanjiDataTableSchema.name
)
data class KanjiDataEntity(
    @PrimaryKey
    @ColumnInfo(name = KanjiDataTableSchema.Columns.kanji) val kanji: String,
    @ColumnInfo(name = KanjiDataTableSchema.Columns.frequency) val frequency: Int?
)