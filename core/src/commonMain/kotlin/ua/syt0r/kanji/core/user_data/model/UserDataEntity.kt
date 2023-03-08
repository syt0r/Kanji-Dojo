package ua.syt0r.kanji.core.user_data.model

data class Practice(
    val id: Long,
    val name: String
)

data class CharacterReviewResult(
    val character: String,
    val practiceId: Long,
    val mistakes: Int
)