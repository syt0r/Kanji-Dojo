package ua.syt0r.kanji.presentation.screen.main.screen.backup

import ua.syt0r.kanji.core.backup.PlatformFile

interface BackupContract {

    interface ViewModel {
        fun createBackup(file: PlatformFile)
    }

}