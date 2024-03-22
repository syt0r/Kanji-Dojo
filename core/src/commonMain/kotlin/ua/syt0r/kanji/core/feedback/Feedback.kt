package ua.syt0r.kanji.core.feedback

import kotlinx.serialization.json.JsonObject

data class FeedbackRequestData(
    val topic: String,
    val message: String
)

interface FeedbackManager {
    suspend fun sendFeedback(data: FeedbackRequestData): Result<Unit>
}

interface FeedbackUserDataProvider {
    suspend fun provide(): JsonObject
}