package ua.syt0r.kanji.presentation.screen.main.screen.backup

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.backup.BackupManager
import ua.syt0r.kanji.core.backup.PlatformFile

class BackupViewModel(
    private val viewModelScope: CoroutineScope,
    private val backupManager: BackupManager
) : BackupContract.ViewModel {

    override fun createBackup(file: PlatformFile) {
        viewModelScope.launch {
            backupManager.performBackup(file)
        }
    }

}