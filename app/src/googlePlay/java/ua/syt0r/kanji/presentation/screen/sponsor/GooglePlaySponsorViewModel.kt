package ua.syt0r.kanji.presentation.screen.sponsor

import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.billing.BillingManager
import ua.syt0r.kanji.core.billing.DonationOffer
import ua.syt0r.kanji.core.billing.PurchaseResult
import ua.syt0r.kanji.presentation.screen.sponsor.GooglePlaySponsorScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.sponsor.use_case.GooglePlaySendSponsorResultsUseCase

class GooglePlaySponsorViewModel(
    private val viewModelScope: CoroutineScope,
    private val billingManager: BillingManager,
    private val sendSponsorResultsUseCase: GooglePlaySendSponsorResultsUseCase,
    private val analyticsManager: AnalyticsManager
) : GooglePlaySponsorScreenContract.ViewModel {

    private val email = mutableStateOf("")
    private val message = mutableStateOf("")

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Init)
    override val state: StateFlow<ScreenState> = _state

    private var purchasesJson: List<String>? = null

    override fun loadInputState() {
        _state.value = ScreenState.Loading

        viewModelScope.launch {
            billingManager.getDonationOffers()
                .onSuccess {
                    _state.value = ScreenState.Input(
                        email = email,
                        message = message,
                        buttonEnabled = mutableStateOf(true),
                        offers = it
                    )
                }
                .onFailure {
                    val errorMessage = it.message ?: UNKNOWN_ERROR_MESSAGE
                    _state.value = ScreenState.Error(
                        message = "Couldn't load offers: $errorMessage"
                    )
                    analyticsManager.sendEvent("billing_init_error") {
                        put("error", errorMessage)
                    }
                }
        }

    }

    override fun startPurchase(activity: Activity, offer: DonationOffer) {
        viewModelScope.launch {
            when (val result = offer.startPurchaseFlow(activity)) {
                PurchaseResult.Canceled -> {
                    val inputState = _state.value as ScreenState.Input
                    inputState.buttonEnabled.value = true
                    analyticsManager.sendEvent("purchase_canceled")
                }

                is PurchaseResult.Error -> _state.value =
                    ScreenState.Error(message = result.message)

                is PurchaseResult.Success -> {
                    purchasesJson = result.purchaseJsonList
                    notifyBackend(result.purchaseJsonList)
                }
            }
        }
    }


    override fun retry(activity: Activity) {
        val purchasesJson = purchasesJson
        if (purchasesJson != null) {
            viewModelScope.launch { notifyBackend(purchasesJson) }
        } else {
            loadInputState()
        }
        analyticsManager.sendEvent("sponsor_retry")
    }

    override fun reportScreenShown() {
        analyticsManager.setScreen("sponsor")
    }

    private suspend fun notifyBackend(purchasesJson: List<String>) {
        _state.value = ScreenState.Loading
        val result = sendSponsorResultsUseCase(
            email = email.value,
            message = message.value,
            purchasesJson = purchasesJson
        )
        if (result.isSuccess) {
            _state.value = ScreenState.Completed
            analyticsManager.sendEvent("sponsor_purchase_complete")
        } else {
            val errorMessage = result.exceptionOrNull()!!.message ?: UNKNOWN_ERROR_MESSAGE
            _state.value = ScreenState.Error(errorMessage)
            analyticsManager.sendEvent("sponsor_notify_result_error") {
                put("error", errorMessage)
            }
        }
    }

    companion object {
        private const val UNKNOWN_ERROR_MESSAGE = "unknown_error"
    }

}