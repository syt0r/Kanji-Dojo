package ua.syt0r.kanji.core.backup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import ua.syt0r.kanji.core.suspended_property.SuspendedPropertiesBackupManager
import ua.syt0r.kanji.core.user_data.UserDataDatabaseManager
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class DefaultBackupManager(
    private val platformFileHandler: PlatformFileHandler,
    private val userDataDatabaseManager: UserDataDatabaseManager,
    private val suspendedPropertiesBackupManager: SuspendedPropertiesBackupManager
) : BackupManager {

    companion object {
        private const val BACKUP_INFO_FILENAME = "backup_info.json"
        private const val PREFERENCES_FILENAME = "user_preferences.json"
    }

    private val json = Json { prettyPrint = true }

    override suspend fun performBackup(location: PlatformFile) {
        userDataDatabaseManager.doWithSuspendedConnection { databaseInfo ->
            val outputStream = platformFileHandler.getOutputStream(location)
            ZipOutputStream(outputStream).use {
                val backupInfo = BackupInfo(
                    databaseVersion = databaseInfo.version,
                    backupCreateTimestamp = Clock.System.now().toEpochMilliseconds(),
                    userDatabaseFileName = databaseInfo.file.name
                )
                it.writeJsonFile(BACKUP_INFO_FILENAME, backupInfo)

                val preferences = suspendedPropertiesBackupManager.exportModifiedProperties()
                it.writeJsonFile(PREFERENCES_FILENAME, preferences)

                it.writeFile(databaseInfo.file)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readBackupInfo(
        location: PlatformFile
    ): BackupInfo = withContext(Dispatchers.IO) {
        val inputStream = platformFileHandler.getInputStream(location)
        ZipInputStream(inputStream).use {
            it.findZipEntry { zipEntry -> zipEntry.name == BACKUP_INFO_FILENAME }
                ?: throw IllegalStateException("Backup info not found")
            json.decodeFromStream<BackupInfo>(it)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun restore(location: PlatformFile) = withContext(Dispatchers.IO) {
        val databaseFileName = readBackupInfo(location).userDatabaseFileName
        val inputStream = platformFileHandler.getInputStream(location)

        ZipInputStream(inputStream).use {

            fun ZipEntry.isDbOrPreferences(): Boolean =
                name == databaseFileName || name == PREFERENCES_FILENAME

            suspend fun ZipEntry.handleEntry() = when (name) {
                databaseFileName -> {
                    userDataDatabaseManager.replaceDatabase(it)
                }

                PREFERENCES_FILENAME -> {
                    suspendedPropertiesBackupManager.importProperties(
                        jsonObject = json.decodeFromStream(it)
                    )
                }

                else -> throw IllegalArgumentException("Unsupported backup file")
            }

            val firstEntry = it.findZipEntry { zipEntry -> zipEntry.isDbOrPreferences() }
            firstEntry!!.handleEntry()

            val secondEntry = it.findZipEntry { zipEntry -> zipEntry.isDbOrPreferences() }
            secondEntry!!.handleEntry()

        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private inline fun <reified T> ZipOutputStream.writeJsonFile(fileName: String, data: T) {
        putNextEntry(ZipEntry(fileName))
        json.encodeToStream(data, this)
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
