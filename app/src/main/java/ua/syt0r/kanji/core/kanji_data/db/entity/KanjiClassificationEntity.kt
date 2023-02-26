package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.common.db.schema.KanjiClassificationTableSchema
import ua.syt0r.kanji.common.db.schema.KanjiClassificationTableSchema.Columns

@Entity(
    tableName = KanjiClassificationTableSchema.name,
    primaryKeys = [Columns.kanji, Columns.classification]
)
data class KanjiClassificationEntity(
    @ColumnInfo(name = Columns.kanji) val kanji: String,
    @ColumnInfo(name = Columns.classification) val classification: CharactersClassification
)