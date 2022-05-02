package ua.syt0r.kanji.core.user_data.db.entity

import androidx.room.ColumnInfo
import java.time.LocalDateTime

class ReviewedPracticeEntity(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "latest_review_timestamp") val timestamp: LocalDateTime?
)