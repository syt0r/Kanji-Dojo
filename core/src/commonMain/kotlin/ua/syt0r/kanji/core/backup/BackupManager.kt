package ua.syt0r.kanji.core.backup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import ua.syt0r.kanji.core.user_data.UserDataDatabaseManager
import ua.syt0r.kanji.core.user_data.UserDatabaseInfo
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

interface BackupManager {
    suspend fun performBackup(location: PlatformFile)
    suspend fun readBackupInfo(location: PlatformFile): BackupInfo
    suspend fun restore(location: PlatformFile)
}

// TODO make `expect` class when out of beta
interface PlatformFile

abstract class BaseBackupManager(
    private val userDataDatabaseManager: UserDataDatabaseManager
) : BackupManager {

    companion object {
        private const val BACKUP_INFO_FILENAME = "backup_info.json"
    }

    private val json = Json { prettyPrint = true }

    abstract fun getInputStream(platformFile: PlatformFile): InputStream
    abstract fun getOutputStream(platformFile: PlatformFile): OutputStream

    override suspend fun performBackup(location: PlatformFile) {
        userDataDatabaseManager.doWithSuspendedConnection { databaseInfo ->
            ZipOutputStream(getOutputStream(location)).use {
                it.addBackupInfoEntry(databaseInfo)
                it.writeFile(databaseInfo.file)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readBackupInfo(
        location: PlatformFile
    ): BackupInfo = withContext(Dispatchers.IO) {
        ZipInputStream(getInputStream(location)).use {
            it.findZipEntry { zipEntry -> zipEntry.name == BACKUP_INFO_FILENAME }
                ?: throw IllegalStateException("Backup info not found")
            json.decodeFromStream<BackupInfo>(it)
        }
    }

    override suspend fun restore(location: PlatformFile) = withContext(Dispatchers.IO) {
        val databaseFileName = readBackupInfo(location).userDatabaseFileName

        ZipInputStream(getInputStream(location)).use {
            it.findZipEntry { zipEntry -> zipEntry.name == databaseFileName }
                ?: throw IllegalStateException("Database not found")
            userDataDatabaseManager.replaceDatabase(it)
        }
    }


    @OptIn(ExperimentalSerializationApi::class)
    private fun ZipOutputStream.addBackupInfoEntry(databaseInfo: UserDatabaseInfo) {
        putNextEntry(ZipEntry(BACKUP_INFO_FILENAME))
        val backupInfo = BackupInfo(
            databaseVersion = databaseInfo.version,
            backupCreateTimestamp = Clock.System.now().toEpochMilliseconds(),
            userDatabaseFileName = databaseInfo.file.name
        )
        json.encodeToStream(backupInfo, this)
        flush()
        closeEntry()
    }

    private fun ZipOutputStream.writeFile(file: File) {
        putNextEntry(ZipEntry(file.name))
        file.inputStream().transferTo(this)
        flush()
        closeEntry()
    }

    private fun ZipInputStream.findZipEntry(predicate: (ZipEntry) -> Boolean): ZipEntry? {
        var currentEntry = nextEntry
        while (currentEntry != null && !predicate(currentEntry)) {
            currentEntry = nextEntry
        }
        return currentEntry
    }

}
