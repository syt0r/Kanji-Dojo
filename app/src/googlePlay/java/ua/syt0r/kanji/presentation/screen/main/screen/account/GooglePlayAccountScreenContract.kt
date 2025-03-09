package ua.syt0r.kanji.presentation.screen.main.screen.account

import android.app.Activity
import com.android.billingclient.api.BillingFlowParams
import kotlinx.coroutines.flow.StateFlow
import ua.syt0r.kanji.core.ApiRequestIssue
import ua.syt0r.kanji.core.SubscriptionInfo

interface GooglePlayAccountScreenContract {

    interface ViewModel {
        val state: StateFlow<ScreenState>
        fun signIn(data: AccountScreenContract.ScreenData)
        fun signOut()
        fun refresh()
    }

    sealed interface ScreenState {
        data object Loading : ScreenState
        data object SignedOut : ScreenState
        data class SignedIn(
            val email: String,
            val subscriptionInfo: SubscriptionInfo,
            val issue: ApiRequestIssue?,
            val subscriptionSectionState: SubscriptionSectionState,
        ) : ScreenState

        data class Error(
            val issue: ApiRequestIssue
        ) : ScreenState
    }

}

sealed interface SubscriptionSectionState {

    data object Hidden : SubscriptionSectionState

    data class Shown(
        val content: StateFlow<SubscriptionSectionContentState>
    ) : SubscriptionSectionState

}

sealed interface SubscriptionSectionContentState {

    data class ShowingOffers(
        val offersState: StateFlow<SubscriptionOffersState>,
    ) : SubscriptionSectionContentState

    data object Loading : SubscriptionSectionContentState

    data class PurchaseCompleted(
        val refreshAccountInfo: () -> Unit
    ) : SubscriptionSectionContentState

    data class PurchaseError(
        val message: String,
        val retry: () -> Unit
    ) : SubscriptionSectionContentState
}

sealed interface SubscriptionOffersState {

    data object Loading : SubscriptionOffersState

    data class Offers(
        val offers: List<DisplaySubscriptionOffer>,
        val subscribe: (Activity, DisplaySubscriptionOffer) -> Unit
    ) : SubscriptionOffersState

    data class Error(
        val message: String,
        val retry: () -> Unit
    ) : SubscriptionOffersState

}

data class DisplaySubscriptionOffer(
    val formattedPeriod: String,
    val formattedPrice: String,
    val billingFlowParams: BillingFlowParams
)
