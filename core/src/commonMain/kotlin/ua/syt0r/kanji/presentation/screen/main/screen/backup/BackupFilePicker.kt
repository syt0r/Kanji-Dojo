package ua.syt0r.kanji.presentation.screen.main.screen.backup

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ua.syt0r.kanji.core.file.PlatformFile


sealed interface FilePickResult {
    data class Picked(val file: PlatformFile) : FilePickResult
    object Canceled : FilePickResult
}

const val BackupMimeType = "application/zip"
fun getDefaultBackupFileName(): String {
    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        .run { "$year-$monthNumber-$dayOfMonth-$hour-$minute" }
    return "kanji-dojo-backup-${currentTime}.zip"
}
