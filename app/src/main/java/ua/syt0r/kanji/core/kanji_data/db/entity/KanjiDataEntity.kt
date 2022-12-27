package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ua.syt0r.kanji.common.db.KanjiDataTable

@Entity(
    tableName = KanjiDataTable.name
)
data class KanjiDataEntity(
    @PrimaryKey
    @ColumnInfo(name = KanjiDataTable.Columns.kanji) val kanji: String,
    @ColumnInfo(name = KanjiDataTable.Columns.frequency) val frequency: Int?,
    @ColumnInfo(name = KanjiDataTable.Columns.grade) val grade: Int?,
    @ColumnInfo(name = KanjiDataTable.Columns.jlpt) val jlpt: String?
)