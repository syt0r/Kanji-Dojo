package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.AccountManager
import ua.syt0r.kanji.core.ApiRequestIssue
import ua.syt0r.kanji.core.emitWhenWithSubscribers
import ua.syt0r.kanji.core.format
import ua.syt0r.kanji.core.sync.SyncConflictResolveStrategy
import ua.syt0r.kanji.core.sync.SyncDataDiffType
import ua.syt0r.kanji.core.sync.SyncFeatureState
import ua.syt0r.kanji.core.sync.SyncManager
import ua.syt0r.kanji.core.sync.SyncState
import ua.syt0r.kanji.core.user_data.database.DatabaseMigrationState
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract
import ua.syt0r.kanji.core.user_data.preferences.PreferencesContract
import ua.syt0r.kanji.presentation.common.resources.string.getStrings
import ua.syt0r.kanji.snackbar_sub_expired_action
import ua.syt0r.kanji.snackbar_sub_expired_message

class MainScreenViewModel(
    private val viewModelScope: CoroutineScope,
    appPreferences: PreferencesContract.AppPreferences,
    accountManager: AccountManager,
    migrationObservable: UserDataDatabaseContract.MigrationObservable,
    private val syncManager: SyncManager
) : MainContract.ViewModel {

    private val _notifications = MutableSharedFlow<MainSnackbarNotification>()
    override val notifications: SharedFlow<MainSnackbarNotification> = _notifications

    override val migrationState: StateFlow<DatabaseMigrationState> = migrationObservable.state

    private val _syncDialogState = MutableStateFlow<SyncDialogState>(SyncDialogState.Hidden)
    override val syncDialogState: StateFlow<SyncDialogState> = _syncDialogState

    init {

        appPreferences.subscriptionAlert.onModified
            .distinctUntilChanged()
            .drop(1)
            .filterNotNull()
            .map { notifySubscriptionAlert(it) }
            .launchIn(viewModelScope)

        accountManager.subscriptionExpirationEvents
            .onEach { notifySubscriptionExpiration() }
            .launchIn(viewModelScope)

        syncManager.state
            .flatMapLatest { syncDialogStateFlow(it) }
            .onEach { _syncDialogState.value = it }
            .launchIn(viewModelScope)
    }

    override fun cancelSync() {
        when (val syncState = syncManager.state.value) {
            SyncFeatureState.Disabled,
            SyncFeatureState.Loading -> Unit

            is SyncFeatureState.Enabled -> syncState.cancel()
            is SyncFeatureState.Error -> _syncDialogState.value = SyncDialogState.Hidden
        }
    }

    override fun resolveSyncConflict(strategy: SyncConflictResolveStrategy) {
        when (val syncState = syncManager.state.value) {
            is SyncFeatureState.Enabled -> syncState.resolveConflict(strategy)
            else -> Unit
        }
    }

    private fun syncDialogStateFlow(
        syncFeatureState: SyncFeatureState
    ) = channelFlow<SyncDialogState> {
        when (syncFeatureState) {
            SyncFeatureState.Disabled,
            SyncFeatureState.Loading -> {
                send(SyncDialogState.Hidden)
            }

            is SyncFeatureState.Error -> {
                send(SyncDialogState.Error.Api(syncFeatureState.issue))
            }

            is SyncFeatureState.Enabled -> {
                syncFeatureState.state
                    .onEach { handleEnabledSyncState(it) }
                    .collect()
            }
        }
    }

    private suspend fun ProducerScope<SyncDialogState>.handleEnabledSyncState(
        syncState: SyncState
    ) {
        val a = when (syncState) {
            SyncState.Refreshing,
            SyncState.Canceled,
            is SyncState.TrackingChanges -> SyncDialogState.Hidden

            SyncState.Uploading -> SyncDialogState.Uploading
            SyncState.Downloading -> SyncDialogState.Downloading
            is SyncState.Conflict -> {
                when (syncState.diffType) {
                    SyncDataDiffType.RemoteUnsupported -> {
                        notifySyncError(SyncDialogState.Error.Unsupported)
                        SyncDialogState.Hidden
                    }

                    else -> SyncDialogState.Conflict(
                        diffType = syncState.diffType,
                        remoteDataTime = syncState.remoteDataInfo.dataTimestamp
                            ?.let { Instant.fromEpochMilliseconds(it) }
                            ?.toLocalDateTime(TimeZone.currentSystemDefault()),
                        lastSyncTime = syncState.cachedDataInfo?.dataTimestamp
                            ?.let { Instant.fromEpochMilliseconds(it) }
                            ?.toLocalDateTime(TimeZone.currentSystemDefault())
                    )
                }
            }

            is SyncState.Error.Api -> {
                notifySyncError(SyncDialogState.Error.Api(syncState.issue))
                SyncDialogState.Hidden
            }
        }
        send(a)
    }

    private suspend fun notifySubscriptionAlert(alert: String) {
        val notification = MainSnackbarNotification(
            message = alert,
            isError = false,
            handleAction = { MainDestination.Account() },
            duration = SnackbarDuration.Indefinite
        )
        _notifications.emitWhenWithSubscribers(notification)
    }

    private suspend fun notifySubscriptionExpiration() {
        val notification = MainSnackbarNotification(
            message = getString(Res.string.snackbar_sub_expired_message),
            actionLabel = getString(Res.string.snackbar_sub_expired_action),
            isError = false,
            handleAction = { MainDestination.Account() },
            duration = SnackbarDuration.Indefinite
        )
        _notifications.emitWhenWithSubscribers(notification)
    }

    private fun notifySyncError(error: SyncDialogState.Error) {
        val strings = getStrings().syncSnackbar
        val issueDescription = when (error) {
            is SyncDialogState.Error.Api -> {
                when (error.issue) {
                    ApiRequestIssue.NoConnection -> strings.errorNoConnection
                    ApiRequestIssue.NoSubscription -> strings.errorNoSubscription
                    ApiRequestIssue.NotAuthenticated -> strings.errorNotAuthenticated
                    is ApiRequestIssue.Other -> null
                }
            }

            is SyncDialogState.Error.Unsupported -> strings.errorDataNotSupported
        }

        val snackbarMessage = when {
            issueDescription != null -> strings.errorMessageTemplate.format(issueDescription)
            else -> strings.errorMessageNoReason
        }

        val notification = MainSnackbarNotification(
            message = snackbarMessage,
            isError = true,
            actionLabel = strings.actionButton,
            handleAction = { _syncDialogState.value = error; null }
        )

        viewModelScope.launch { _notifications.emitWhenWithSubscribers(notification) }
    }

}
