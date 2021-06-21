package ua.syt0r.kanji.core.user_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "practice_set_entry",
    primaryKeys = ["kanji", "practice_set_id"],
    foreignKeys = [
        ForeignKey(
            entity = PracticeSetEntity::class,
            parentColumns = ["id"],
            childColumns = ["practice_set_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PracticeSetEntryEntity(
    @ColumnInfo(name = "kanji") val kanji: String,
    @ColumnInfo(name = "practice_set_id") val practiceSetId: Long
)