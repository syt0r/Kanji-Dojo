package ua.syt0r.kanji.core.user_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDateTime

@Entity(
    tableName = "reading_review",
    primaryKeys = [
        "character",
        "practice_id",
        "timestamp",
        "mistakes"
    ],
    foreignKeys = [
        ForeignKey(
            entity = PracticeEntity::class,
            parentColumns = ["id"],
            childColumns = ["practice_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReadingReviewEntity(
    @ColumnInfo(name = "character") val character: String,
    @ColumnInfo(name = "practice_id") val practiceSetId: Long,
    @ColumnInfo(name = "timestamp") val reviewTime: LocalDateTime,
    @ColumnInfo(name = "mistakes") val mistakes: Int
)
