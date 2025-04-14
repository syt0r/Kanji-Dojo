package ua.syt0r.kanji.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalUriHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.module.Module
import ua.syt0r.kanji.core.AccountManager
import ua.syt0r.kanji.core.AccountState
import ua.syt0r.kanji.core.ApiRequestIssue
import ua.syt0r.kanji.core.SubscriptionInfo
import ua.syt0r.kanji.presentation.IosAccountScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.account.AccountScreenContainer
import ua.syt0r.kanji.presentation.screen.main.screen.account.AccountScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.account.AccountScreenError
import ua.syt0r.kanji.presentation.screen.main.screen.account.AccountScreenLoading
import ua.syt0r.kanji.presentation.screen.main.screen.account.AccountScreenSignedIn
import ua.syt0r.kanji.presentation.screen.main.screen.account.AccountScreenSignedOut

fun Module.addAccountScreenComponents() {

    single<AccountScreenContract.Content> { IosAccountScreenContent }

    multiplatformViewModel {
        IosAccountScreenViewModel(
            coroutineScope = it.component1(),
            accountManager = get()
        )
    }

}

object IosAccountScreenContent : AccountScreenContract.Content {

    @Composable
    override fun invoke(
        state: MainNavigationState,
        data: AccountScreenContract.ScreenData?
    ) {

        val viewModel = getMultiplatformViewModel<IosAccountScreenViewModel>()
        val uriHandler = LocalUriHandler.current

        LaunchedEffect(Unit) {
            if (data != null) viewModel.signIn(data)
        }

        AccountScreenUI(
            state = viewModel.state.collectAsState(),
            onUpClick = { state.navigateBack() },
            signIn = { uriHandler.openUri(AccountScreenContract.DEEP_LINK_AUTH_URL) },
            signOut = { viewModel.signOut() },
            refresh = { viewModel.refresh() }
        )
    }

}

interface IosAccountScreenContract {

    sealed interface ScreenState {
        object SignedOut : ScreenState
        object Loading : ScreenState

        data class Loaded(
            val email: String,
            val subscriptionInfo: SubscriptionInfo,
            val issue: ApiRequestIssue?
        ) : ScreenState

        data class Error(
            val issue: ApiRequestIssue
        ) : ScreenState

    }

}

@Composable
fun AccountScreenUI(
    state: State<ScreenState>,
    onUpClick: () -> Unit,
    signIn: () -> Unit,
    signOut: () -> Unit,
    refresh: () -> Unit
) {

    AccountScreenContainer(
        state = state,
        onUpClick = onUpClick
    ) { screenState ->

        when (screenState) {
            ScreenState.SignedOut -> {
                AccountScreenSignedOut(
                    startSignIn = signIn
                )
            }

            ScreenState.Loading -> {
                AccountScreenLoading()
            }

            is ScreenState.Loaded -> {
                AccountScreenSignedIn(
                    email = screenState.email,
                    subscriptionInfo = screenState.subscriptionInfo,
                    issue = screenState.issue,
                    refresh = refresh,
                    signOut = signOut,
                    signIn = signIn
                )
            }

            is ScreenState.Error -> {
                AccountScreenError(
                    issue = screenState.issue,
                    startSignIn = signIn
                )
            }
        }

    }

}

class IosAccountScreenViewModel(
    coroutineScope: CoroutineScope,
    private val accountManager: AccountManager
) {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val state: StateFlow<ScreenState> = _state

    init {

        accountManager.state
            .onEach {
                _state.value = when (it) {
                    AccountState.Loading -> ScreenState.Loading
                    AccountState.LoggedOut -> ScreenState.SignedOut
                    is AccountState.LoggedIn -> ScreenState.Loaded(
                        email = it.email,
                        subscriptionInfo = it.subscriptionInfo,
                        issue = it.issue
                    )

                    is AccountState.Error -> ScreenState.Error(it.issue)
                }
            }
            .launchIn(coroutineScope)

    }

    fun signIn(data: AccountScreenContract.ScreenData) {
        accountManager.signIn(data.refreshToken, data.idToken)
    }

    fun signOut() {
        accountManager.signOut()
    }

    fun refresh() {
        accountManager.refreshUserData()
    }

}
