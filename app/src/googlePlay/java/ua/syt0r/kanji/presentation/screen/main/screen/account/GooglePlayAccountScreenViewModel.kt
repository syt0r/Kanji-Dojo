package ua.syt0r.kanji.presentation.screen.main.screen.account

import com.android.billingclient.api.BillingClient.BillingResponseCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
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
import ua.syt0r.kanji.core.billing.BillingManager
import ua.syt0r.kanji.core.billing.BillingState
import ua.syt0r.kanji.presentation.screen.main.screen.account.GooglePlayAccountScreenContract.ScreenState


class GooglePlayAccountScreenViewModel(
    viewModelScope: CoroutineScope,
    private val accountManager: AccountManager,
    private val billingManager: BillingManager
) : GooglePlayAccountScreenContract.ViewModel {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loading)
    override val state: StateFlow<ScreenState> = _state

    init {

        accountManager.state
            .flatMapLatest {
                when (it) {
                    AccountState.Loading -> flowOf(ScreenState.Loading)
                    AccountState.LoggedOut -> flowOf(ScreenState.SignedOut)
                    is AccountState.LoggedIn -> channelFlow {
                        coroutineScope {
                            val screenState = ScreenState.SignedIn(
                                email = it.email,
                                subscriptionInfo = it.subscriptionInfo,
                                issue = it.issue,
                                subscriptionSectionState = when {
//                                    it.subscriptionInfo is SubscriptionInfo.Active -> {
//                                        SubscriptionSectionState.Hidden
//                                    }

                                    else -> SubscriptionSectionState.Shown(
                                        createSubscriptionSectionContentState()
                                    )
                                }
                            )
                            send(screenState)
                        }

                    }

                    is AccountState.Error -> flowOf(ScreenState.Error(it.issue))
                }
            }
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

    private fun CoroutineScope.createSubscriptionSectionContentState(): StateFlow<SubscriptionSectionContentState> {
        val offersContentState = SubscriptionSectionContentState.ShowingOffers(
            offersState = createSubscriptionOffersState()
        )

        val contentState = MutableStateFlow<SubscriptionSectionContentState>(offersContentState)

        billingManager.purchaseUpdates
            .onEach {
                when (it.billingResult.responseCode) {
                    BillingResponseCode.OK -> {

                    }

                    BillingResponseCode.USER_CANCELED -> {
                        contentState.value = offersContentState
                    }

                    else -> {
                        contentState.value = SubscriptionSectionContentState.PurchaseError(
                            message = "Purchase error",
                            retry = { contentState.value = offersContentState }
                        )
                    }
                }
            }
            .launchIn(this)

        return contentState
    }

    private fun CoroutineScope.createSubscriptionOffersState(): StateFlow<SubscriptionOffersState> {
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
            .launchIn(this)

        return subscriptionOffersState
    }

    private fun BillingState.Connected.loadOffersFlow(retry: () -> Unit) = flow {
        emit(SubscriptionOffersState.Loading)

        val offers = billingManager.getSubscriptionOffers().getOrElse {
            emit(SubscriptionOffersState.Error(it.message ?: "No error message", retry))
            return@flow
        }

        val state = SubscriptionOffersState.Offers(
            offers = offers.map {
                DisplaySubscriptionOffer(
                    formattedPeriod = getFormattedBillingPeriodMessage(it.period),
                    formattedPrice = it.formattedPrice,
                    billingFlowParams = it.billingFlowParams
                )
            },
            subscribe = { activity, offer ->
                billingClient.launchBillingFlow(activity, offer.billingFlowParams)
            }
        )
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
