package  ua.syt0r.kanji.presentation.screen.main.screen.home

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.sync.SyncFeatureState
import ua.syt0r.kanji.core.sync.SyncManager
import ua.syt0r.kanji.core.sync.SyncState
import ua.syt0r.kanji.core.user_data.preferences.PreferencesContract
import ua.syt0r.kanji.core.user_data.preferences.PreferencesDefaultHomeTab

class HomeViewModel(
    viewModelScope: CoroutineScope,
    private val appPreferences: PreferencesContract.AppPreferences,
    private val syncManager: SyncManager
) : HomeScreenContract.ViewModel {

    override val defaultTab: HomeScreenTab = runBlocking {
        when (appPreferences.defaultHomeTab.get()) {
            PreferencesDefaultHomeTab.GeneralDashboard -> HomeScreenTab.GeneralDashboard
            PreferencesDefaultHomeTab.Letters -> HomeScreenTab.LettersDashboard
            PreferencesDefaultHomeTab.Vocab -> HomeScreenTab.VocabDashboard
        }
    }

    private val _syncIconState = MutableStateFlow(SyncIconState())
    override val syncIconState: StateFlow<SyncIconState> = _syncIconState

    init {

        syncManager.state
            .flatMapLatest { it.toSyncIconStateFlow() }
            .onEach {
                Logger.d("syncIconState[$it]")
                _syncIconState.value = it
            }
            .launchIn(viewModelScope)

    }

    override fun trySync(): Boolean {
        when (val syncState = syncManager.state.value) {
            SyncFeatureState.Disabled,
            SyncFeatureState.Loading,
            is SyncFeatureState.Error -> return false

            is SyncFeatureState.Enabled -> {
                syncState.sync()
                return true
            }
        }
    }

    private fun SyncFeatureState.toSyncIconStateFlow(): Flow<SyncIconState> {
        return when (this) {
            SyncFeatureState.Disabled -> flowOf(SyncIconState())
            SyncFeatureState.Loading -> flowOf(SyncIconState(true))
            is SyncFeatureState.Error -> flowOf(SyncIconState(false, SyncIconIndicator.Error))
            is SyncFeatureState.Enabled -> toSyncIconStateFlow()
        }
    }

    private fun SyncFeatureState.Enabled.toSyncIconStateFlow(): Flow<SyncIconState> {
        return state.flatMapLatest {
            when (it) {
                SyncState.Refreshing,
                SyncState.Uploading,
                SyncState.Downloading -> flowOf(SyncIconState(true))

                is SyncState.TrackingChanges -> it.uploadAvailable.map { uploadAvailable ->
                    SyncIconState(
                        loading = false,
                        indicator = when {
                            uploadAvailable -> SyncIconIndicator.PendingUpload
                            else -> SyncIconIndicator.UpToDate
                        }
                    )
                }

                SyncState.Canceled -> flowOf(SyncIconState(false, SyncIconIndicator.Canceled))
                is SyncState.Conflict -> flowOf(SyncIconState(false, SyncIconIndicator.Disabled))
                is SyncState.Error -> flowOf(SyncIconState(false, SyncIconIndicator.Error))
            }
        }
    }

}