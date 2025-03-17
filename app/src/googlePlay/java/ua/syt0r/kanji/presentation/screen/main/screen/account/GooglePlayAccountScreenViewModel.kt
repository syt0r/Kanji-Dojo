package ua.syt0r.kanji.presentation.screen.main.screen.account

import com.android.billingclient.api.BillingClient.BillingResponseCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.DateTimePeriod
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.account_subscription_duration_days
import ua.syt0r.kanji.account_subscription_duration_months
import ua.syt0r.kanji.account_subscription_duration_unknown
import ua.syt0r.kanji.account_subscription_duration_years
import ua.syt0r.kanji.core.AccountManager
import ua.syt0r.kanji.core.AccountState
import ua.syt0r.kanji.core.NetworkApi
import ua.syt0r.kanji.core.SubscriptionInfo
import ua.syt0r.kanji.core.billing.BillingManager
import ua.syt0r.kanji.core.billing.BillingState
import ua.syt0r.kanji.core.billing.PurchasesUpdate
import ua.syt0r.kanji.presentation.screen.main.screen.account.GooglePlayAccountScreenContract.ScreenState


class GooglePlayAccountScreenViewModel(
    viewModelScope: CoroutineScope,
    private val accountManager: AccountManager,
    private val billingManager: BillingManager,
    private val networkApi: NetworkApi
) : GooglePlayAccountScreenContract.ViewModel {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loading)
    override val state: StateFlow<ScreenState> = _state

    init {

        accountManager.state
            .flatMapLatest { it.toScreenStateFlow() }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)

    }

    override fun signIn(data: AccountScreenContract.ScreenData) {
        accountManager.signIn(data.refreshToken, data.idToken)
    }

    override fun signOut() {
        accountManager.signOut()
    }

    override fun refresh() {
        accountManager.refreshUserData()
    }

    private fun AccountState.toScreenStateFlow() = when (this) {
        AccountState.Loading -> flowOf(ScreenState.Loading)
        AccountState.LoggedOut -> flowOf(ScreenState.SignedOut)
        is AccountState.LoggedIn -> channelFlow {
            coroutineScope {
                val screenState = ScreenState.SignedIn(
                    email = email,
                    subscriptionInfo = subscriptionInfo,
                    issue = issue,
                    subscriptionSectionState = when {
                        subscriptionInfo is SubscriptionInfo.Active -> {
                            SubscriptionSectionState.Hidden
                        }

                        else -> SubscriptionSectionState.Shown(
                            content = createSubscriptionSectionContentState(this)
                        )
                    }
                )
                send(screenState)
            }

        }

        is AccountState.Error -> flowOf(ScreenState.Error(issue))
    }

    private fun createSubscriptionSectionContentState(
        coroutineScope: CoroutineScope
    ): StateFlow<SubscriptionSectionContentState> {
        val offersContentState = SubscriptionSectionContentState.ShowingOffers(
            offersState = createSubscriptionOffersState(coroutineScope)
        )

        val contentState = MutableStateFlow<SubscriptionSectionContentState>(offersContentState)

        val attemptState = MutableStateFlow(Unit)
        billingManager.purchaseUpdates
            .combine(attemptState) { purchasesUpdate, _ -> purchasesUpdate }
            .flatMapLatest { purchasesUpdate ->
                handlePurchaseUpdatesFlow(
                    purchasesUpdate = purchasesUpdate,
                    contentState = contentState,
                    offersContentState = offersContentState,
                    retry = { attemptState.value = Unit }
                )
            }
            .launchIn(coroutineScope)

        return contentState
    }

    private fun handlePurchaseUpdatesFlow(
        purchasesUpdate: PurchasesUpdate,
        contentState: MutableStateFlow<SubscriptionSectionContentState>,
        offersContentState: SubscriptionSectionContentState.ShowingOffers,
        retry: () -> Unit
    ) = channelFlow {
        when (purchasesUpdate.billingResult.responseCode) {
            BillingResponseCode.OK -> {
                send(SubscriptionSectionContentState.Loading)
                val result = runCatching { purchasesUpdate.purchases.first() }
                    .mapCatching { networkApi.postSubscription(it.originalJson) }
                    .map {
                        SubscriptionSectionContentState.PurchaseCompleted(
                            refreshAccountInfo = { accountManager.refreshUserData() }
                        )
                    }.getOrElse {
                        SubscriptionSectionContentState.PurchaseError(
                            message = "Error processing purchase: ${it.message}.",
                            retry = retry
                        )
                    }

                send(result)
            }

            BillingResponseCode.USER_CANCELED -> {
                send(offersContentState)
            }

            else -> {
                send(
                    SubscriptionSectionContentState.PurchaseError(
                        message = "Purchase error",
                        retry = { contentState.value = offersContentState }
                    )
                )
            }
        }
    }

    private fun createSubscriptionOffersState(
        coroutineScope: CoroutineScope
    ): StateFlow<SubscriptionOffersState> {
        val subscriptionOffersState = MutableStateFlow<SubscriptionOffersState>(
            value = SubscriptionOffersState.Loading
        )

        val offersLoadingAttempts = MutableStateFlow(Unit)

        offersLoadingAttempts
            .flatMapLatest { billingManager.state }
            .flatMapLatest {
                when (it) {
                    is BillingState.Connected -> {
                        it.loadOffersFlow { offersLoadingAttempts.tryEmit(Unit) }
                    }

                    is BillingState.Disconnected -> {
                        flowOf(
                            SubscriptionOffersState.Error(
                                message = "Couldn't establish connection with Google Play",
                                retry = it.retry
                            )
                        )
                    }

                    BillingState.Loading -> {
                        flowOf(SubscriptionOffersState.Loading)
                    }
                }
            }
            .onEach { subscriptionOffersState.value = it }
            .launchIn(coroutineScope)

        return subscriptionOffersState
    }

    private fun BillingState.Connected.loadOffersFlow(retry: () -> Unit) = flow {
        emit(SubscriptionOffersState.Loading)

        val state = networkApi.getUserId()
            .mapCatching { billingManager.getSubscriptionOffers(it) }
            .map { offers ->
                offers.map {
                    DisplaySubscriptionOffer(
                        formattedPeriod = getFormattedBillingPeriodMessage(it.period),
                        formattedPrice = it.formattedPrice,
                        billingFlowParams = it.billingFlowParams
                    )
                }
            }
            .map { displayOffers ->
                SubscriptionOffersState.Offers(
                    offers = displayOffers,
                    subscribe = { activity, offer ->
                        billingClient.launchBillingFlow(activity, offer.billingFlowParams)
                    }
                )
            }
            .getOrElse {
                SubscriptionOffersState.Error(
                    message = it.message ?: "No error message",
                    retry = retry
                )
            }

        emit(state)
    }

    private suspend fun getFormattedBillingPeriodMessage(dateTimePeriod: DateTimePeriod): String {
        return when {
            dateTimePeriod.years > 0 -> {
                getPluralString(
                    Res.plurals.account_subscription_duration_years,
                    dateTimePeriod.years,
                    dateTimePeriod.years
                )
            }

            dateTimePeriod.months > 0 -> {
                getPluralString(
                    Res.plurals.account_subscription_duration_months,
                    dateTimePeriod.months,
                    dateTimePeriod.months
                )
            }

            dateTimePeriod.days > 0 -> {
                getPluralString(
                    Res.plurals.account_subscription_duration_days,
                    dateTimePeriod.days,
                    dateTimePeriod.days
                )
            }

            else -> getString(
                Res.string.account_subscription_duration_unknown,
                dateTimePeriod.toString()
            )
        }
    }

}
