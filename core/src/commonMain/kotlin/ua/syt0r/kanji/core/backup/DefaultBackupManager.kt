package ua.syt0r.kanji.core.backup

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import ua.syt0r.kanji.core.file.PlatformFile
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract
import ua.syt0r.kanji.core.user_data.preferences.PreferencesBackupManager

class DefaultBackupManager(
    private val userDataDatabaseManager: UserDataDatabaseContract.Manager,
    private val preferencesBackupManager: PreferencesBackupManager,
    private val archiveHandler: BackupArchiveHandler,
    private val restoreCompletionNotifier: BackupRestoreCompletionNotifier,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BackupManager {

    override suspend fun backupTo(location: PlatformFile) = withContext(dispatcher) {
        userDataDatabaseManager.doWithSuspendedConnection { databaseInfo ->
            val backupInfo = BackupInfo(
                databaseVersion = databaseInfo.version,
                backupCreateTimestamp = Clock.System.now().toEpochMilliseconds(),
                userDatabaseFileName = "database.sqlite"
            )

            archiveHandler.writeBackupZip(
                backupInfo = backupInfo,
                preferences = preferencesBackupManager.exportPreferences(),
                database = databaseInfo.file,
                output = location
            )
        }
    }

    override suspend fun readInfoFrom(
        location: PlatformFile
    ): BackupInfo = withContext(dispatcher) {
        archiveHandler.readZipBackupInfo(location)
    }

    override suspend fun restoreFrom(location: PlatformFile) = withContext(dispatcher) {
        archiveHandler.readBackupZip(location) {
            Logger.d("importing preferences")
            preferencesBackupManager.importPreferences(jsonObject = it.preferences)
            Logger.d("replaceDatabase")
            userDataDatabaseManager.replaceDatabase(it.databaseReadChannel)
            Logger.d("replaceDatabase completed")
        }
        restoreCompletionNotifier.notify()
        Logger.d("restoreCompletionNotifier notified")
    }

}
