package ua.syt0r.kanji.core.backup

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.flow.SharedFlow

interface BackupManager {
    suspend fun performBackup(location: PlatformFile)
    suspend fun readBackupInfo(location: PlatformFile): BackupInfo
    suspend fun restore(location: PlatformFile)
}

interface BackupRestoreCompletionNotifier {
    suspend fun notify()
}

interface BackupRestoreEventsProvider {
    val onRestoreEventsFlow: SharedFlow<Unit>
}

// TODO make `expect` class when out of beta
interface PlatformFile

interface PlatformFileHandler {
    fun getInputStream(file: PlatformFile): ByteReadChannel
    fun getOutputStream(file: PlatformFile): ByteWriteChannel
}
