package ua.syt0r.kanji.core.backup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import ua.syt0r.kanji.core.theme_manager.ThemeManager
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract
import ua.syt0r.kanji.core.user_data.preferences.PreferencesBackupManager

class DefaultBackupManager(
    private val platformFileHandler: PlatformFileHandler,
    private val userDataDatabaseManager: UserDataDatabaseContract.Manager,
    private val preferencesBackupManager: PreferencesBackupManager,
    private val themeManager: ThemeManager,
    private val restoreCompletionNotifier: BackupRestoreCompletionNotifier
) : BackupManager {

    companion object {
        private const val BACKUP_INFO_FILENAME = "backup_info.json"
        private const val PREFERENCES_FILENAME = "user_preferences.json"
    }

    private val json = Json { prettyPrint = true }

    override suspend fun performBackup(location: PlatformFile) {
        userDataDatabaseManager.doWithSuspendedConnection { databaseInfo ->
//            TODO ios
//            val outputStream = platformFileHandler.getOutputStream(location)
//            ZipOutputStream(outputStream).use {
//                val backupInfo = BackupInfo(
//                    databaseVersion = databaseInfo.version,
//                    backupCreateTimestamp = Clock.System.now().toEpochMilliseconds(),
//                    userDatabaseFileName = databaseInfo.file.name
//                )
//                it.writeJsonFile(BACKUP_INFO_FILENAME, backupInfo)
//
//                val preferences = preferencesBackupManager.exportPreferences()
//                it.writeJsonFile(PREFERENCES_FILENAME, preferences)
//
//                it.writeFile(databaseInfo.file)
//            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readBackupInfo(
        location: PlatformFile
    ): BackupInfo = withContext(Dispatchers.IO) {
        val inputStream = platformFileHandler.getInputStream(location)
        TODO()
//        ZipInputStream(inputStream).use {
//            it.findZipEntry { zipEntry -> zipEntry.name == BACKUP_INFO_FILENAME }
//                ?: throw IllegalStateException("Backup info not found")
//            json.decodeFromStream<BackupInfo>(it)
//        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun restore(location: PlatformFile) = withContext(Dispatchers.IO) {
//        val preferences: JsonObject = ZipInputStream(
//            platformFileHandler.getInputStream(location)
//        ).use {
//            it.findZipEntry { zipEntry -> zipEntry.name == PREFERENCES_FILENAME }
//            json.decodeFromStream(it)
//        }
//        preferencesBackupManager.importPreferences(jsonObject = preferences)
//
//        val databaseFileName = readBackupInfo(location).userDatabaseFileName
//        ZipInputStream(platformFileHandler.getInputStream(location)).use {
//            it.findZipEntry { zipEntry -> zipEntry.name == databaseFileName }
//            userDataDatabaseManager.replaceDatabase(it)
//        }

        themeManager.invalidate()
        restoreCompletionNotifier.notify()
    }

//    @OptIn(ExperimentalSerializationApi::class)
//    private inline fun <reified T> ZipOutputStream.writeJsonFile(fileName: String, data: T) {
//        putNextEntry(ZipEntry(fileName))
//        json.encodeToStream(data, this)
//        flush()
//        closeEntry()
//    }
//
//    private fun ZipOutputStream.writeFile(file: File) {
//        putNextEntry(ZipEntry(file.name))
//        file.inputStream().transferToCompat(this)
//        flush()
//        closeEntry()
//    }
//
//    private fun ZipInputStream.findZipEntry(predicate: (ZipEntry) -> Boolean): ZipEntry? {
//        var currentEntry = nextEntry
//        while (currentEntry != null && !predicate(currentEntry)) {
//            currentEntry = nextEntry
//        }
//        return currentEntry
//    }

}
