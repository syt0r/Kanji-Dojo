package ua.syt0r.kanji.ios

import io.ktor.utils.io.ByteReadChannel
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import ua.syt0r.kanji.core.backup.BackupArchiveData
import ua.syt0r.kanji.core.backup.BackupArchiveHandler
import ua.syt0r.kanji.core.backup.BackupArchiveSchema
import ua.syt0r.kanji.core.backup.BackupInfo
import ua.syt0r.kanji.core.deleteRecursively
import ua.syt0r.kanji.core.file.PlatformFile
import ua.syt0r.kanji.core.getPrivateAppDataDirPath
import ua.syt0r.kanji.core.logger.Logger

abstract class BaseIosBackupArchiveHandler : BackupArchiveHandler {

    private val json = Json { prettyPrint = true }

    override suspend fun writeBackupZip(
        backupInfo: BackupInfo,
        preferencesJson: JsonObject,
        database: PlatformFile,
        output: PlatformFile
    ) {
        createBackup(
            backupInfoJson = json.encodeToString(backupInfo),
            preferencesJson = json.encodeToString(preferencesJson),
            userDataDatabasePath = database.url,
            userDataDatabaseFileName = backupInfo.userDatabaseFileName,
            outputZipPath = output.url
        )
    }

    override suspend fun readZipBackupInfo(file: PlatformFile): BackupInfo {
        return readBackupInfoJson(file.url).let { json.decodeFromString(it) }
    }

    override suspend fun readBackupZip(
        file: PlatformFile,
        action: suspend (BackupArchiveData) -> Unit
    ) {
        Logger.d("readBackupZip file[$file]")
        val unpackedZipPathString = getPrivateAppDataDirPath() + "/tmp"
        val unpackedZipPath = Path(unpackedZipPathString)

        runCatching {
            Logger.d("readBackupZip preparing")
            SystemFileSystem.deleteRecursively(unpackedZipPath, mustExist = false)
            SystemFileSystem.createDirectories(unpackedZipPath)

            Logger.d("readBackupZip unpacking")
            unpackBackupTo(file.url, unpackedZipPathString)

            Logger.d("readBackupZip reading backupInfo")
            val backupInfo: BackupInfo = SystemFileSystem
                .source(Path(unpackedZipPath, BackupArchiveSchema.BACKUP_INFO_FILENAME))
                .buffered()
                .readString()
                .let { json.decodeFromString(it) }

            Logger.d("readBackupZip reading preferences")
            val preferences: JsonObject = SystemFileSystem
                .source(Path(unpackedZipPath, BackupArchiveSchema.PREFERENCES_FILENAME))
                .buffered()
                .readString()
                .let { json.decodeFromString(it) }

            val databaseFilePath = Path(unpackedZipPath, backupInfo.userDatabaseFileName)

            Logger.d("readBackupZip reading database file")
            val backupArchiveData = BackupArchiveData(
                backupInfo = backupInfo,
                preferences = preferences,
                databaseReadChannel = ByteReadChannel(
                    SystemFileSystem.source(databaseFilePath).buffered()
                )
            )

            action(backupArchiveData)

            Logger.d("readBackupZip deleting tmp")
            SystemFileSystem.deleteRecursively(unpackedZipPath, mustExist = false)
        }.getOrElse {
            Logger.d("readBackupZip deleting tmp")
            SystemFileSystem.deleteRecursively(unpackedZipPath, mustExist = false)
            throw it
        }
    }

    @Throws(Exception::class)
    protected abstract fun createBackup(
        backupInfoJson: String,
        preferencesJson: String,
        userDataDatabasePath: String,
        userDataDatabaseFileName: String,
        outputZipPath: String
    )

    @Throws(Exception::class)
    protected abstract fun readBackupInfoJson(
        zipPath: String
    ): String

    @Throws(Exception::class)
    protected abstract fun unpackBackupTo(
        zipPath: String,
        destinationPath: String
    )

}