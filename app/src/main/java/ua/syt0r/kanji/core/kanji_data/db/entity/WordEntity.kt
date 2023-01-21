package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ua.syt0r.kanji.common.db.schema.DictionaryEntryTableSchema

@Entity(tableName = DictionaryEntryTableSchema.name)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DictionaryEntryTableSchema.Columns.id) val id: Long?,
)
