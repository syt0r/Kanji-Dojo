package ua.syt0r.kanji.core.billing

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

data class SubscriptionOffer(
    val period: DateTimePeriod,
    val formattedPrice: String,
    val billingFlowParams: BillingFlowParams
)
