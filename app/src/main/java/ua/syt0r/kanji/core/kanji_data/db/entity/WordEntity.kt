package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ua.syt0r.kanji.common.db.entity.FuriganaDBEntity
import ua.syt0r.kanji.common.db.schema.WordTableSchema

@Entity(tableName = WordTableSchema.name)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = WordTableSchema.Columns.id) val id: Long?,
    @ColumnInfo(name = WordTableSchema.Columns.expression) val expression: String,
    @ColumnInfo(name = WordTableSchema.Columns.furigana) val furiganaDBField: FuriganaDBField,
    @ColumnInfo(name = WordTableSchema.Columns.priority) val priority: Int
)

data class FuriganaDBField(
    val furigana: List<FuriganaDBEntity>
)