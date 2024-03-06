package ua.syt0r.kanji.presentation.screen.main.screen.backup

import androidx.compose.runtime.Composable
import kotlinx.datetime.Clock
import ua.syt0r.kanji.core.backup.PlatformFile


interface BackupFilePicker {
    fun startCreateFileFlow()
    fun startSelectFileFlow()
}

sealed interface FilePickResult {
    data class Picked(val file: PlatformFile) : FilePickResult
    object Canceled : FilePickResult
}

const val BackupMimeType = "application/zip"
fun getDefaultBackupFileName(): String {
    val currentTime = Clock.System.now()
    return "kanji-dojo-backup-${currentTime.epochSeconds}.zip"
}

@Composable
fun rememberBackupFilePicker(
    onFileCreateCallback: ((FilePickResult) -> Unit)? = null,
    onFileSelectCallback: ((FilePickResult) -> Unit)? = null
): BackupFilePicker = internalRememberBackupFilePicker(onFileCreateCallback, onFileSelectCallback)

@Composable
internal expect fun internalRememberBackupFilePicker(
    onFileCreateCallback: ((FilePickResult) -> Unit)?,
    onFileSelectCallback: ((FilePickResult) -> Unit)?
): BackupFilePicker