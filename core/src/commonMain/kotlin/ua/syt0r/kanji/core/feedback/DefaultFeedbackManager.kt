package ua.syt0r.kanji.core.feedback

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class DefaultFeedbackManager(
    private val httpClient: HttpClient,
    private val userDataProvider: FeedbackUserDataProvider,
) : FeedbackManager {

    companion object {
        private const val FeedbackEndpoint = "https://kanji-dojo.com/api/v1/feedback"
    }

    override suspend fun sendFeedback(data: FeedbackRequestData): Result<Unit> {
        return runCatching {
            val requestBody = JsonObject(
                mapOf(
                    "topic" to JsonPrimitive(data.topic),
                    "text" to JsonPrimitive(data.message),
                    "user" to userDataProvider.provide()
                )
            )

            val response = httpClient.post(FeedbackEndpoint) {
                contentType(ContentType.Application.Json)
                setBody(requestBody.toString())
            }

            if (!response.status.isSuccess())
                throw Throwable(response.status.description)
        }
    }

}
