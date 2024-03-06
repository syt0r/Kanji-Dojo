package ua.syt0r.kanji.core.backup

import java.io.File

interface BackupManager {
    fun performBackup(location: PlatformFile)
    fun restore(location: PlatformFile)
}


interface PlatformFile
data class PlatformFileJvm(val file: File) : PlatformFile

class DefaultBackupManager : BackupManager {
    override fun performBackup(location: PlatformFile) {

    }

    override fun restore(location: PlatformFile) {

    }
}