package ua.syt0r.kanji.core.backup

import java.io.InputStream
import java.io.OutputStream

interface BackupManager {
    suspend fun performBackup(location: PlatformFile)
    suspend fun readBackupInfo(location: PlatformFile): BackupInfo
    suspend fun restore(location: PlatformFile)
}

// TODO make `expect` class when out of beta
interface PlatformFile

interface PlatformFileHandler {
    fun getInputStream(file: PlatformFile): InputStream
    fun getOutputStream(file: PlatformFile): OutputStream
}
