package ua.syt0r.kanji.core.backup

import ua.syt0r.kanji.core.user_data.UserDataDatabaseManager
import java.io.File
import java.io.InputStream
import java.io.OutputStream

data class PlatformFileJvm(val file: File) : PlatformFile

class BackupManagerJvm(
    userDataDatabaseManager: UserDataDatabaseManager
) : BaseBackupManager(userDataDatabaseManager) {

    override fun getInputStream(platformFile: PlatformFile): InputStream {
        platformFile as PlatformFileJvm
        return platformFile.file.inputStream()
    }

    override fun getOutputStream(platformFile: PlatformFile): OutputStream {
        platformFile as PlatformFileJvm
        return platformFile.file.outputStream()
    }

}
