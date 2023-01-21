package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ua.syt0r.kanji.common.db.entity.FuriganaDBEntity
import ua.syt0r.kanji.common.db.schema.DictionaryReadingTableSchema
import ua.syt0r.kanji.common.db.schema.DictionaryReadingTableSchema.Columns

@Entity(tableName = DictionaryReadingTableSchema.name)
data class WordReadingEntity(
    @PrimaryKey @ColumnInfo(name = Columns.rowId) val rowId: Long?,
    @ColumnInfo(name = Columns.dictionaryEntryId) val dictionaryEntryId: Long,
    @ColumnInfo(name = Columns.kanjiExpression) val expression: String?,
    @ColumnInfo(name = Columns.kanaExpression) val kanaExpression: String,
    @ColumnInfo(name = Columns.furigana) val furiganaDBField: FuriganaDBField?,
    @ColumnInfo(name = Columns.rank) val rank: Int
)

data class FuriganaDBField(
    val furigana: List<FuriganaDBEntity>
)