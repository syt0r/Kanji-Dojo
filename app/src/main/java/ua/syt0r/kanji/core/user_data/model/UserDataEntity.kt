package ua.syt0r.kanji.core.user_data.model

import java.time.LocalDateTime

data class PracticeSetInfo(
    val id: Long,
    val name: String,
    val previewKanji: String,
    val latestReviewTime: LocalDateTime
)

enum class ReviewResult {
    LEARN,
    REVIEW,
    REPEAT
}

data class KanjiReviewInfo(
    val kanji: String,
    val practiceSetId: Long,
    val reviewTime: LocalDateTime,
    val reviewInterval: Long,
    val reviewResult: ReviewResult
)