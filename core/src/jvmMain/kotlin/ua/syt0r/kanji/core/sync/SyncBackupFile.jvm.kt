package ua.syt0r.kanji.core.sync

import ua.syt0r.kanji.core.file.PlatformFile
import ua.syt0r.kanji.core.getUserDataDirectory
import java.io.File

class JvmSyncBackupFileProvider : SyncBackupFileProvider {
    override fun invoke() = PlatformFile(
        javaFile = File(getUserDataDirectory(), "sync_backup.zip")
    )
}