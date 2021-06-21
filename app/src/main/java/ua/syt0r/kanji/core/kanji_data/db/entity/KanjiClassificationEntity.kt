package ua.syt0r.kanji.core.kanji_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji_db_model.db.KanjiClassificationTable.CLASS_COLUMN
import ua.syt0r.kanji_db_model.db.KanjiClassificationTable.CLASS_GROUP_COLUMN
import ua.syt0r.kanji_db_model.db.KanjiClassificationTable.KANJI_COLUMN
import ua.syt0r.kanji_db_model.db.KanjiClassificationTable.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = [KANJI_COLUMN, CLASS_GROUP_COLUMN, CLASS_COLUMN],
)
data class KanjiClassificationEntity(
    @ColumnInfo(name = KANJI_COLUMN) val kanji: String,
    @ColumnInfo(name = CLASS_GROUP_COLUMN) val classGroup: String,
    @ColumnInfo(name = CLASS_COLUMN) val className: String
)