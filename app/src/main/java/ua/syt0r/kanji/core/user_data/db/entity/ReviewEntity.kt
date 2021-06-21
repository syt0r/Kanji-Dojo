package ua.syt0r.kanji.core.user_data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ua.syt0r.kanji.core.user_data.model.ReviewResult
import java.time.LocalDateTime

@Entity(
    tableName = "review",
    primaryKeys = [
        "kanji",
        "practice_set_id",
        "review_time",
        "interval",
        "result"
    ]
)
data class ReviewEntity(
    @ColumnInfo(name = "kanji") val kanji: String,
    @ColumnInfo(name = "practice_set_id") val practiceSetId: Long,
    @ColumnInfo(name = "review_time") val reviewTime: LocalDateTime,
    @ColumnInfo(name = "interval") val interval: Long,
    @ColumnInfo(name = "result") val reviewResult: ReviewResult
)
