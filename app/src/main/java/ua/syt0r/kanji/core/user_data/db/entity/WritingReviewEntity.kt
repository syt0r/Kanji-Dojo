package ua.syt0r.kanji.core.user_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDateTime

@Entity(
    tableName = "writing_review",
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
data class WritingReviewEntity(
    @ColumnInfo(name = "character") val character: String,
    @ColumnInfo(name = "practice_id") val practiceSetId: Long,
    @ColumnInfo(name = "timestamp") val reviewTime: LocalDateTime,
    @ColumnInfo(name = "mistakes") val mistakes: Int,
    @ColumnInfo(name = "is_study") val isStudy: Boolean
)
