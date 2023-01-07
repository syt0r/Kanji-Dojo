package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji.common.db.schema.KanjiReadingTableSchema

@Entity(
    tableName = KanjiReadingTableSchema.name,
    primaryKeys = [KanjiReadingTableSchema.Columns.kanji, KanjiReadingTableSchema.Columns.readingType, KanjiReadingTableSchema.Columns.reading],
)
data class KanjiReadingEntity(
    @ColumnInfo(name = KanjiReadingTableSchema.Columns.kanji) val kanji: String,
    @ColumnInfo(name = KanjiReadingTableSchema.Columns.readingType) val type: KanjiReadingTableSchema.ReadingType,
    @ColumnInfo(name = KanjiReadingTableSchema.Columns.reading) val reading: String
)