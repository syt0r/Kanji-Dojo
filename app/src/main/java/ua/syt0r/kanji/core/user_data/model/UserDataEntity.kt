package ua.syt0r.kanji.core.user_data.model

import java.time.LocalDateTime

data class Practice(
    val id: Long,
    val name: String
)

data class RecentPractice(
    val id: Long,
    val name: String,
    val timestamp: LocalDateTime
)

data class KanjiWritingReview(
    val kanji: String,
    val practiceSetId: Long,
    val reviewTime: LocalDateTime,
    val mistakes: Int
)