package ua.syt0r.kanji.core.sync

import ua.syt0r.kanji.core.file.PlatformFile
import ua.syt0r.kanji.core.getPrivateAppDataDirPath

class IosSyncBackupFileProvider : SyncBackupFileProvider {
    override fun invoke(): PlatformFile = PlatformFile(
        getPrivateAppDataDirPath() + "sync_backup.zip"
    )
}