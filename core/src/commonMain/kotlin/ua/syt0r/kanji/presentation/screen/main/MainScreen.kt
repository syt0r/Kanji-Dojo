package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import io.ktor.http.Url
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import org.koin.compose.koinInject
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.getMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.features.DeepLinkHandler
import ua.syt0r.kanji.presentation.screen.main.features.MigrationDialog
import ua.syt0r.kanji.presentation.screen.main.features.SyncDialog
import ua.syt0r.kanji.presentation.screen.main.screen.account.AccountScreenContract

@Composable
fun MainScreen(
    deepLinkHandler: DeepLinkHandler
) {

    val viewModel = getMultiplatformViewModel<MainContract.ViewModel>()
    val navigationState = rememberMainNavigationState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { NotificationSnackbar(it) }
            )
        }
    ) {
        MainNavigation(navigationState)
    }

    MigrationDialog(
        state = viewModel.migrationState.collectAsState()
    )

    SyncDialog(
        state = viewModel.syncDialogState.collectAsState(),
        cancelSync = viewModel::cancelSync,
        resolveConflict = viewModel::resolveSyncConflict,
        navigateToAccount = {
            viewModel.cancelSync()
            navigationState.navigate(MainDestination.Account())
        }
    )

    HandleDeepLinksLaunchedEffect(
        deepLinkHandler = deepLinkHandler,
        navigationState = navigationState
    )

    HandleScreenReportsLaunchedEffect(
        navigationState = navigationState
    )

    HandleSnackbarNotificationsLaunchedEffect(
        notifications = viewModel.notifications,
        snackbarHostState = snackbarHostState,
        navigationState = navigationState
    )

}

@Composable
private fun HandleDeepLinksLaunchedEffect(
    deepLinkHandler: DeepLinkHandler,
    navigationState: MainNavigationState
) {

    LaunchedEffect(Unit) {
        deepLinkHandler.deepLinksFlow
            .mapNotNull {
                when {
                    it.startsWith("kanji-dojo://signin") -> {
                        val data = Url(it).parameters.run {
                            AccountScreenContract.ScreenData(
                                refreshToken = get("refreshToken") ?: return@run null,
                                idToken = get("idToken") ?: return@run null
                            )
                        }
                        if (data != null) {
                            MainDestination.Account(data)
                        } else {
                            Logger.d("Couldn't parse deep link $it")
                            null
                        }
                    }

                    else -> {
                        Logger.d("Unsupported deeplink[$it]")
                        null
                    }
                }
            }
            .collectLatest {
                Logger.d("Navigating from deep link to destination[$it]")
                navigationState.navigateToTop(it)
            }
    }

}

@Composable
private fun HandleScreenReportsLaunchedEffect(navigationState: MainNavigationState) {
    val analyticsManager = koinInject<AnalyticsManager>()
    LaunchedEffect(Unit) {
        snapshotFlow { navigationState.currentDestination.value }
            .map { it?.analyticsName }
            .filterNotNull()
            .onEach { analyticsManager.setScreen(it) }
            .launchIn(this)
    }
}

@Composable
private fun HandleSnackbarNotificationsLaunchedEffect(
    notifications: SharedFlow<MainSnackbarNotification>,
    snackbarHostState: SnackbarHostState,
    navigationState: MainNavigationState
) {
    LaunchedEffect(Unit) {
        notifications.collectLatest { notification ->
            val result = snackbarHostState.showSnackbar(notification)
            if (result == SnackbarResult.ActionPerformed) {
                val destination = notification.handleAction()
                if (destination != null) {
                    navigationState.navigate(destination)
                }
            }
        }
    }
}

@Composable
private fun NotificationSnackbar(snackbarData: SnackbarData) {
    val notification = snackbarData.visuals as MainSnackbarNotification
    when {
        notification.isError -> {
            Snackbar(
                snackbarData = snackbarData,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                actionColor = MaterialTheme.colorScheme.onErrorContainer,
                actionContentColor = MaterialTheme.colorScheme.onErrorContainer,
                dismissActionContentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        else -> {
            Snackbar(snackbarData)
        }
    }
}
