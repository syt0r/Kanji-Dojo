package ua.syt0r.kanji.core.user_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDateTime

@Entity(
    tableName = "writing_review",
    primaryKeys = [
        "kanji",
        "practice_set_id",
        "timestamp",
        "mistakes"
    ],
    foreignKeys = [
        ForeignKey(
            entity = PracticeSetEntity::class,
            parentColumns = ["id"],
            childColumns = ["practice_set_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WritingReviewEntity(
    @ColumnInfo(name = "kanji") val kanji: String,
    @ColumnInfo(name = "practice_set_id") val practiceSetId: Long,
    @ColumnInfo(name = "timestamp") val reviewTime: LocalDateTime,
    @ColumnInfo(name = "mistakes") val mistakes: Int
)
