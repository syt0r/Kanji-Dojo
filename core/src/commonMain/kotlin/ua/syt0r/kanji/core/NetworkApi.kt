package ua.syt0r.kanji.core

import io.ktor.client.request.forms.ChannelProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import ua.syt0r.kanji.core.user_data.preferences.PreferencesSyncDataInfo

interface NetworkApi {

    suspend fun getUserInfo(): Result<ApiUserInfo>
    suspend fun getUserId(): Result<String>

    suspend fun getSyncDataInfo(): Result<SubscriptionResponse<ApiSyncDataInfo>>
    suspend fun getSyncData(): Result<SubscriptionResponse<ByteReadChannel>>
    suspend fun updateSyncData(
        syncDataInfo: ApiSyncDataInfo,
        channelProvider: ChannelProvider
    ): Result<SubscriptionResponse<Unit>>

    suspend fun postFeedback(data: FeedbackApiData): Result<Unit>
    suspend fun postDonationPurchase(data: DonationPurchaseApiData): Result<Unit>
    suspend fun getDonations(): Result<List<ApiDonation>>
    suspend fun postSubscription(purchaseJson: String): Result<Unit>

}

data class HttpResponseException(
    val statusCode: HttpStatusCode
) : Throwable()

data class SubscriptionResponse<T>(
    val value: T,
    val alert: String?
)

sealed interface ApiRequestIssue {

    data object NoConnection : ApiRequestIssue
    data object NotAuthenticated : ApiRequestIssue
    data object NoSubscription : ApiRequestIssue
    data class Other(val throwable: Throwable) : ApiRequestIssue

    companion object {
        fun classify(throwable: Throwable): ApiRequestIssue {
            return when (throwable) {
                is UnresolvedAddressException -> NoConnection
                is HttpResponseException -> when (throwable.statusCode) {
                    HttpStatusCode.Unauthorized -> NotAuthenticated
                    HttpStatusCode.PaymentRequired -> NoSubscription
                    else -> Other(throwable)
                }

                else -> {
                    Other(throwable)
                }
            }
        }
    }

}


@Serializable
data class ApiUserInfo(
    val email: String,
    val subscription: Boolean,
    val subscriptionDue: Long? = null
)

@Serializable
data class ApiSyncDataInfo(
    val dataId: String,
    val dataVersion: Long,
    val dataTimestamp: Long? = null
)

fun ApiSyncDataInfo.toPreferencesType() =
    PreferencesSyncDataInfo(dataId, dataVersion, dataTimestamp)

fun PreferencesSyncDataInfo.toApiType() = ApiSyncDataInfo(dataId, dataVersion, dataTimestamp)

data class FeedbackApiData(
    val topic: String,
    val message: String,
    val userData: JsonObject
)

data class DonationPurchaseApiData(
    val email: String,
    val message: String,
    val purchasesJson: List<String>
)

@Serializable
data class ApiDonation(
    val time: Long,
    val amountJpy: Float
)

class DefaultNetworkApi(
    private val networkClients: NetworkClients,
    private val json: Json
) : NetworkApi {

    override suspend fun getUserInfo(): Result<ApiUserInfo> {
        return safeRequest { networkClients.authenticatedClient.get(GET_USER_INFO_URL) }
            .mapCatching { json.decodeFromString(it.bodyAsText()) }
    }

    override suspend fun getUserId(): Result<String> {
        return safeRequest { networkClients.authenticatedClient.get(GET_USER_ID_URL) }
            .mapCatching { it.bodyAsText() }
    }

    override suspend fun getSyncDataInfo(): Result<SubscriptionResponse<ApiSyncDataInfo>> {
        return safeSubscriptionRequest(
            request = { networkClients.authenticatedClient.get(GET_SYNC_INFO_URL) },
            responseMapper = { json.decodeFromString<ApiSyncDataInfo>(it.bodyAsText()) }
        )
    }

    override suspend fun getSyncData(): Result<SubscriptionResponse<ByteReadChannel>> {
        return safeSubscriptionRequest(
            request = {
                networkClients.authenticatedClient.get(GET_SYNC_URL)
            },
            responseMapper = { it.bodyAsChannel() }
        )
    }


    override suspend fun updateSyncData(
        syncDataInfo: ApiSyncDataInfo,
        channelProvider: ChannelProvider
    ): Result<SubscriptionResponse<Unit>> = safeSubscriptionRequest(
        request = {
            val infoJson = json.encodeToString(syncDataInfo)
            networkClients.authenticatedClient.post(UPDATE_SYNC_URL) {
                val partDataList = formData {
                    append("info", infoJson)
                    append("data", channelProvider, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"data.zip\"")
                    })
                }
                setBody(MultiPartFormDataContent(partDataList))
            }
        },
        responseMapper = { Unit }
    )

    override suspend fun postFeedback(data: FeedbackApiData) = safeRequestUnit {
        val requestBody = JsonObject(
            mapOf(
                "topic" to JsonPrimitive(data.topic),
                "text" to JsonPrimitive(data.message),
                "user" to data.userData
            )
        )

        networkClients.unauthenticatedClient.post(FEEDBACK_URL) {
            contentType(ContentType.Application.Json)
            setBody(requestBody.toString())
        }
    }

    override suspend fun postDonationPurchase(data: DonationPurchaseApiData) = safeRequestUnit {
        val requestBody = JsonObject(
            mapOf(
                "email" to JsonPrimitive(data.email),
                "message" to JsonPrimitive(data.message),
                "paymentsJson" to JsonArray(
                    content = data.purchasesJson.map { JsonPrimitive(it) }
                )
            )
        )

        networkClients.unauthenticatedClient.post(SPONSOR_URL) {
            contentType(ContentType.Application.Json)
            setBody(requestBody.toString())
        }
    }

    override suspend fun getDonations(): Result<List<ApiDonation>> {
        return safeRequest { networkClients.unauthenticatedClient.get(DONATIONS_URL) }
            .mapCatching { json.decodeFromString(it.bodyAsText()) }
    }

    override suspend fun postSubscription(purchaseJson: String): Result<Unit> = safeRequestUnit {
        networkClients.authenticatedClient.post(SUBSCRIPTION_URL) {
            contentType(ContentType.Application.Json)
            setBody(purchaseJson)
        }
    }

    private suspend fun safeRequest(
        block: suspend () -> HttpResponse
    ): Result<HttpResponse> {
        return runCatching {
            val response = block()
            if (response.status != HttpStatusCode.OK) throw HttpResponseException(response.status)
            response
        }
    }

    private suspend fun safeRequestUnit(
        block: suspend () -> HttpResponse
    ): Result<Unit> {
        return safeRequest(block).mapCatching {}
    }

    private suspend fun <T> safeSubscriptionRequest(
        request: suspend () -> HttpResponse,
        responseMapper: suspend (HttpResponse) -> T
    ): Result<SubscriptionResponse<T>> {
        return safeRequest(request).mapCatching {
            SubscriptionResponse(
                value = responseMapper(it),
                alert = it.headers[SUBSCRIPTION_ALERT_HTTP_HEADER_NAME]
            )
        }
    }

    private companion object {

        const val BASE = "https://kanji-dojo.com/api/v2"

        const val GET_USER_INFO_URL = "$BASE/user/info"
        const val GET_USER_ID_URL = "$BASE/user/id"
        const val GET_SYNC_INFO_URL = "$BASE/sync/info"
        const val GET_SYNC_URL = "$BASE/sync/get"
        const val UPDATE_SYNC_URL = "$BASE/sync/update"
        const val FEEDBACK_URL = "$BASE/feedback"
        const val SPONSOR_URL = "$BASE/sponsor"
        const val DONATIONS_URL = "$BASE/donations"
        const val SUBSCRIPTION_URL = "$BASE/play-billing-subscription"

        const val SUBSCRIPTION_ALERT_HTTP_HEADER_NAME = "X-Subscription-Alert"

    }

}
