package ua.syt0r.kanji.core.sync

import ua.syt0r.kanji.core.file.PlatformFile

interface SyncBackupFileProvider {
    operator fun invoke(): PlatformFile
}