package ua.syt0r.kanji.core.user_data.db.entity

import androidx.room.ColumnInfo
import java.time.LocalDateTime

class LatestWritingReviewCharacterEntity(
    @ColumnInfo(name = "character") val character: String,
    @ColumnInfo(name = "timestamp") val timestamp: LocalDateTime
)