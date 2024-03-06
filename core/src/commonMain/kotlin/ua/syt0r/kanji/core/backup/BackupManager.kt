package ua.syt0r.kanji.core.backup

import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import ua.syt0r.kanji.core.user_data.UserDataDatabaseManager
import ua.syt0r.kanji.core.user_data.UserDatabaseInfo
import java.io.File
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

interface BackupManager {
    suspend fun performBackup(location: PlatformFile)
    fun restore(location: PlatformFile)
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

    abstract fun getOutputStream(platformFile: PlatformFile): OutputStream

    override suspend fun performBackup(location: PlatformFile) {
        userDataDatabaseManager.doWithSuspendedConnection { databaseInfo ->
            val zipOutputStream = ZipOutputStream(getOutputStream(location))

            zipOutputStream.addBackupInfoEntry(databaseInfo)
            zipOutputStream.writeFile(databaseInfo.file)

            zipOutputStream.finish()
        }
    }

    override fun restore(location: PlatformFile) {

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

}
