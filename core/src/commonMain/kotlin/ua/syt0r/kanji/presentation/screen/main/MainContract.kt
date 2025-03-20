package ua.syt0r.kanji.presentation.screen.main

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ua.syt0r.kanji.core.sync.SyncConflictResolveStrategy
import ua.syt0r.kanji.core.user_data.database.DatabaseMigrationState

interface MainContract {

    interface ViewModel {
        val notifications: SharedFlow<MainSnackbarNotification>
        val migrationState: StateFlow<DatabaseMigrationState>
        val syncDialogState: StateFlow<SyncDialogState>
        fun cancelSync()
        fun resolveSyncConflict(strategy: SyncConflictResolveStrategy)
    }

}