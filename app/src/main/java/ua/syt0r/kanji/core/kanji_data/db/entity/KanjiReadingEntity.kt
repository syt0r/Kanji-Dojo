package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji_dojo.shared.db.KanjiReadingTable

@Entity(
    tableName = KanjiReadingTable.name,
    primaryKeys = [KanjiReadingTable.Columns.kanji, KanjiReadingTable.Columns.readingType, KanjiReadingTable.Columns.reading],
)
data class KanjiReadingEntity(
    @ColumnInfo(name = KanjiReadingTable.Columns.kanji) val kanji: String,
    @ColumnInfo(name = KanjiReadingTable.Columns.readingType) val type: KanjiReadingTable.ReadingType,
    @ColumnInfo(name = KanjiReadingTable.Columns.reading) val reading: String
)