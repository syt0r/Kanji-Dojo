package ua.syt0r.kanji.core.backup

import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.json.JsonObject
import ua.syt0r.kanji.core.file.PlatformFile

interface BackupManager {
    suspend fun backupTo(location: PlatformFile)
    suspend fun readInfoFrom(location: PlatformFile): BackupInfo
    suspend fun restoreFrom(location: PlatformFile)
}

interface BackupArchiveHandler {

    suspend fun writeBackupZip(
        backupInfo: BackupInfo,
        preferences: JsonObject,
        database: PlatformFile,
        output: PlatformFile
    )

    suspend fun readZipBackupInfo(file: PlatformFile): BackupInfo

    suspend fun readBackupZip(
        file: PlatformFile,
        action: suspend (BackupArchiveData) -> Unit
    )

}

data class BackupArchiveData(
    val backupInfo: BackupInfo,
    val preferences: JsonObject,
    val databaseReadChannel: ByteReadChannel,
)

object BackupArchiveSchema {
    const val BACKUP_INFO_FILENAME = "backup_info.json"
    const val PREFERENCES_FILENAME = "user_preferences.json"
}

interface BackupRestoreCompletionNotifier {
    suspend fun notify()
}

interface BackupRestoreEventsProvider {
    val onRestoreEventsFlow: SharedFlow<Unit>
}

