package ua.syt0r.kanji.core.user_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "practice_entry",
    primaryKeys = ["character", "practice_id"],
    foreignKeys = [
        ForeignKey(
            entity = PracticeEntity::class,
            parentColumns = ["id"],
            childColumns = ["practice_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PracticeEntryEntity(
    @ColumnInfo(name = "character") val character: String,
    @ColumnInfo(name = "practice_id") val practiceId: Long
)