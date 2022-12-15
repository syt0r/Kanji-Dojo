package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ua.syt0r.kanji_dojo.shared.db.FuriganaDBEntity
import ua.syt0r.kanji_dojo.shared.db.WordTable

@Entity(tableName = WordTable.name)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = WordTable.Columns.id) val id: Long?,
    @ColumnInfo(name = WordTable.Columns.expression) val expression: String,
    @ColumnInfo(name = WordTable.Columns.furigana) val furiganaDBField: FuriganaDBField
)

data class FuriganaDBField(
    val furigana: List<FuriganaDBEntity>
)