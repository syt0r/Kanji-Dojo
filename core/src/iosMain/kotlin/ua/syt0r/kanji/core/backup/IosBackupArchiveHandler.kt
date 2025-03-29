package ua.syt0r.kanji.core.backup

import kotlinx.serialization.json.JsonObject
import ua.syt0r.kanji.core.file.PlatformFile

class IosBackupArchiveHandler : BackupArchiveHandler {

    override suspend fun writeBackupZip(
        backupInfo: BackupInfo,
        preferences: JsonObject,
        database: PlatformFile,
        output: PlatformFile
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun readZipBackupInfo(file: PlatformFile): BackupInfo {
        TODO("Not yet implemented")
    }

    override suspend fun readBackupZip(
        file: PlatformFile,
        action: suspend (BackupArchiveData) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}