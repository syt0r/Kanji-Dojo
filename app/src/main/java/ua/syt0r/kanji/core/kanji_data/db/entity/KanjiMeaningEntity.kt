package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji_dojo.shared.db.KanjiMeaningTable

@Entity(
    tableName = KanjiMeaningTable.name,
    primaryKeys = [KanjiMeaningTable.Columns.kanji, KanjiMeaningTable.Columns.meaning, KanjiMeaningTable.Columns.priority],
)
data class KanjiMeaningEntity(
    @ColumnInfo(name = KanjiMeaningTable.Columns.kanji) val kanji: String,
    @ColumnInfo(name = KanjiMeaningTable.Columns.meaning) val meaning: String,
    @ColumnInfo(name = KanjiMeaningTable.Columns.priority) val priority: Int
)