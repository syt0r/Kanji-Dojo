package ua.syt0r.kanji.presentation.screen.main.screen.account

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ua.syt0r.kanji.presentation.common.AppListItem
import ua.syt0r.kanji.presentation.common.asActivity
import ua.syt0r.kanji.presentation.common.clickable
import ua.syt0r.kanji.presentation.common.copyCentered
import ua.syt0r.kanji.presentation.common.rememberUrlHandler
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.theme.neutralTextButtonColors
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.account.GooglePlayAccountScreenContract.ScreenState

object GooglePlayAccountScreenContent : AccountScreenContract.Content {

    @Composable
    override fun invoke(
        state: MainNavigationState,
        data: AccountScreenContract.ScreenData?
    ) {

        val viewModel = getMultiplatformViewModel<GooglePlayAccountScreenContract.ViewModel>()
        val urlHandler = rememberUrlHandler()

        LaunchedEffect(Unit) {
            if (data != null) viewModel.signIn(data)
        }

        GooglePlayAccountScreenUI(
            state = viewModel.state.collectAsState(),
            onUpClick = { state.navigateBack() },
            onSignInClick = { urlHandler.openInBrowser(AccountScreenContract.DEEP_LINK_AUTH_URL) },
            onSignOutClick = { viewModel.signOut() },
            refresh = { viewModel.refresh() }
        )

    }

}

@Composable
fun GooglePlayAccountScreenUI(
    state: State<ScreenState>,
    onUpClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    refresh: () -> Unit
) {

    AccountScreenContainer(
        state = state,
        onUpClick = onUpClick
    ) { screenState ->

        when (screenState) {
            ScreenState.SignedOut -> {
                AccountScreenSignedOut(
                    startSignIn = onSignInClick
                )
            }

            ScreenState.Loading -> {
                AccountScreenLoading()
            }

            is ScreenState.SignedIn -> {

                AccountScreenSignedIn(
                    email = screenState.email,
                    subscriptionInfo = screenState.subscriptionInfo,
                    issue = screenState.issue,
                    refresh = refresh,
                    signOut = onSignOutClick,
                    signIn = onSignInClick
                ) {

                    when (screenState.subscriptionSectionState) {
                        SubscriptionSectionState.Hidden -> {
                            // No-op
                        }

                        is SubscriptionSectionState.Shown -> {
                            val contentState = screenState.subscriptionSectionState.content
                                .collectAsState()
                            SubscriptionSection(contentState)
                        }
                    }

                    AppListItem(
                        headlineContent = {
                            TextButton(
                                onClick = {},
                                colors = ButtonDefaults.neutralTextButtonColors()
                            ) {
                                Text("Manage subscriptions")
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, null)
                            }
                        }
                    )

                }

            }

            is ScreenState.Error -> {
                AccountScreenError(
                    issue = screenState.issue,
                    startSignIn = onSignInClick
                )
            }
        }

    }

}

@Composable
private fun SubscriptionSection(
    state: State<SubscriptionSectionContentState>,
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceDim)
            .padding(20.dp)
    ) {

        AnimatedContent(
            targetState = state.value
        ) {
            when (it) {
                is SubscriptionSectionContentState.ShowingOffers -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val offersState = it.offersState.collectAsState()
                        SubscriptionOffersContent(offersState)
                    }
                }

                SubscriptionSectionContentState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }

                is SubscriptionSectionContentState.PurchaseCompleted -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                            .size(20.dp),
                        tint = MaterialTheme.extraColorScheme.success
                    )

                    LaunchedEffect(Unit) {
                        delay(600)
                        it.refreshAccountInfo()
                    }
                }

                is SubscriptionSectionContentState.PurchaseError -> {
                    Text(it.message)
                    TextButton(it.retry) {
                        Text("Retry")
                    }
                }
            }
        }

    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.SubscriptionOffersContent(
    offersState: State<SubscriptionOffersState>
) {

    Text(
        text = "Subscribe now to",
        style = MaterialTheme.typography.titleMedium
    )

    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.bodyMedium.copyCentered()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.CheckCircle,
                null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Access Cloud Sync Feature"
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.CheckCircle,
                null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Support app development"
            )
        }
    }

    AnimatedContent(
        targetState = offersState.value
    ) {

        Column {
            when (it) {

                SubscriptionOffersState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                    )
                }

                is SubscriptionOffersState.Offers -> {
                    val activity = LocalContext.current.asActivity()!!

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        it.offers.forEach { offer ->
                            OfferItem(
                                offer = offer,
                                onClick = { it.subscribe(activity, offer) }
                            )
                        }
                    }

                }

                is SubscriptionOffersState.Error -> {
                    OfferError(it)
                }
            }
        }
    }

    Text(
        text = buildAnnotatedString {
            withLink(
                LinkAnnotation.Url(
                    url = "https://kanji-dojo.com/terms",
                    styles = TextLinkStyles(SpanStyle(textDecoration = TextDecoration.Underline))
                )
            ) {
                append("Terms & Conditions")
            }
        },
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

}

@Composable
private fun OfferItem(
    offer: DisplaySubscriptionOffer,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = offer.formattedPrice,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = offer.formattedPeriod,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun OfferError(state: SubscriptionOffersState.Error) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(state.retry)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Couldn't load offers: ${state.message}",
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = "Retry",
            style = MaterialTheme.typography.labelSmall
        )
    }
}
