package ua.syt0r.kanji.core.billing

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimePeriod
import ua.syt0r.kanji.core.launchWhenHasSubscribers

class BillingManager(
    context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val _state = MutableStateFlow<BillingState>(BillingState.Loading)
    val state: StateFlow<BillingState> = _state

    private val _purchaseUpdates = MutableSharedFlow<PurchasesUpdate>()
    val purchaseUpdates: SharedFlow<PurchasesUpdate> = _purchaseUpdates

    private val billingClient: BillingClient

    init {

        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            val update = PurchasesUpdate(billingResult, purchases.orEmpty())
            coroutineScope.launch { _purchaseUpdates.emit(update) }
        }

        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .enablePrepaidPlans()
            .build()

        billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases(pendingPurchasesParams)
            .setListener(purchasesUpdatedListener)
            .build()

        val listener = object : BillingClientStateListener {

            override fun onBillingServiceDisconnected() {
                _state.value = BillingState.Disconnected(
                    retry = {
                        _state.value = BillingState.Loading
                        billingClient.startConnection(this)
                    }
                )
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                _state.value = BillingState.Connected(billingClient)
            }

        }

        _state.launchWhenHasSubscribers(coroutineScope) { billingClient.startConnection(listener) }

    }

    suspend fun getSubscriptionOffers(accountId: String): List<SubscriptionOffer> {
        val subscriptionProduct = QueryProductDetailsParams.Product.newBuilder()
            .setProductId("subscription_base")
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        val queryParams = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(subscriptionProduct))
            .build()

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(queryParams)
        }

        if (productDetailsResult.billingResult.responseCode != BillingResponseCode.OK)
            throw Throwable("Billing request error: ${productDetailsResult.billingResult.debugMessage}")

        val subscriptionDetails = productDetailsResult.productDetailsList!!.first()

        return subscriptionDetails.subscriptionOfferDetails!!.map { offerDetail ->
            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(subscriptionDetails)
                .setOfferToken(offerDetail.offerToken)
                .build()

            val pricingPhase = offerDetail.pricingPhases.pricingPhaseList.first()
            val isoBillingPeriod = pricingPhase.billingPeriod

            SubscriptionOffer(
                period = DateTimePeriod.parse(isoBillingPeriod),
                formattedPrice = pricingPhase.formattedPrice,
                billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(listOf(productDetailsParams))
                    .setObfuscatedAccountId(accountId)
                    .build()
            )
        }
    }

}
