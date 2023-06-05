package ua.syt0r.kanji.core.user_data.model

import kotlin.time.Duration

data class Practice(
    val id: Long,
    val name: String
)

enum class CharacterReviewOutcome { Success, Fail }

data class CharacterReviewResult(
    val character: String,
    val practiceId: Long,
    val mistakes: Int,
    val reviewDuration: Duration,
    val outcome: CharacterReviewOutcome
)