package ua.syt0r.kanji.presentation.screen.main.screen.backup

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import ua.syt0r.kanji.core.backup.BackupManager
import ua.syt0r.kanji.core.backup.PlatformFile
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.presentation.screen.main.screen.backup.BackupContract.ScreenState

class BackupViewModel(
    private val viewModelScope: CoroutineScope,
    private val backupManager: BackupManager
) : BackupContract.ViewModel {

    private val screenStateFlow = MutableStateFlow<ScreenState>(ScreenState.Idle)

    override val state: StateFlow<ScreenState> = screenStateFlow

    override fun createBackup(file: PlatformFile) {
        screenStateFlow.value = ScreenState.Loading

        viewModelScope.launch {

            screenStateFlow.value = runCatching {
                backupManager.performBackup(file)
                ScreenState.ActionCompleted
            }.getOrElse {
                ScreenState.Error(it.message)
            }
        }
    }

    override fun readBackupInfo(file: PlatformFile) {
        screenStateFlow.value = ScreenState.Loading

        viewModelScope.launch {
            screenStateFlow.value = runCatching {
                val backupInfo = backupManager.readBackupInfo(file)

                val currentDbVersion = UserDataDatabase.Schema.version
                if (backupInfo.databaseVersion > currentDbVersion)
                    throw IllegalStateException("Can't import database with newer schema (${backupInfo.databaseVersion}) than current ($currentDbVersion)")

                ScreenState.RestoreConfirmation(
                    file = file,
                    currentDbVersion = currentDbVersion,
                    backupDbVersion = backupInfo.databaseVersion,
                    backupCreateInstant = Instant.fromEpochMilliseconds(backupInfo.backupCreateTimestamp)
                )
            }.getOrElse {
                ScreenState.Error(it.message)
            }
        }
    }

    override fun restoreFromBackup() {
        val currentScreenState = screenStateFlow.value as? ScreenState.RestoreConfirmation
            ?: return

        screenStateFlow.value = ScreenState.Restoring
        viewModelScope.launch {
            screenStateFlow.value = runCatching {
                backupManager.restore(currentScreenState.file)
                ScreenState.ActionCompleted
            }.getOrElse {
                ScreenState.Error(it.message)
            }
        }
    }

}