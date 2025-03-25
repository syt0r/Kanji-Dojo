package ua.syt0r.kanji.core.billing

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import kotlinx.datetime.DateTimePeriod


sealed interface BillingState {

    data object Loading : BillingState

    data class Disconnected(
        val retry: () -> Unit
    ) : BillingState

    data class Connected(
        val billingClient: BillingClient
    ) : BillingState

}

data class PurchasesUpdate(
    val billingResult: BillingResult,
    val purchases: List<Purchase>
)

sealed interface PurchaseResult {
    data class Success(val purchaseJsonList: List<String>) : PurchaseResult
    data object Canceled : PurchaseResult
    data class Error(val message: String) : PurchaseResult
}

data class DonationOffer(
    val formattedPrice: String,
    val startPurchaseFlow: suspend (Activity) -> PurchaseResult
)

data class SubscriptionOffer(
    val period: DateTimePeriod,
    val formattedPrice: String,
    val billingFlowParams: BillingFlowParams
)
