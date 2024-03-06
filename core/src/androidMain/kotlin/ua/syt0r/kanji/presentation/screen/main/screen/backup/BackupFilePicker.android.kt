package ua.syt0r.kanji.presentation.screen.main.screen.backup

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ua.syt0r.kanji.core.backup.PlatformFileAndroid

private class AndroidBackupFilePicker(
    private val create: ManagedActivityResultLauncher<String, Uri?>?,
    private val select: ManagedActivityResultLauncher<Array<String>, Uri?>?
) : BackupFilePicker {

    override fun startCreateFileFlow() {
        create!!.launch(getDefaultBackupFileName())
    }

    override fun startSelectFileFlow() {
        select!!.launch(arrayOf(BackupMimeType))
    }

}

@Composable
internal actual fun internalRememberBackupFilePicker(
    onFileCreateCallback: ((FilePickResult) -> Unit)?,
    onFileSelectCallback: ((FilePickResult) -> Unit)?
): BackupFilePicker {
    val fileCreateLauncher = onFileCreateCallback?.let { callback ->
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument(BackupMimeType)
        ) { callback.invoke(it.toFilePickResult()) }
    }

    val fileSelectLauncher = onFileSelectCallback?.let { callback ->
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { callback.invoke(it.toFilePickResult()) }
    }

    return remember { AndroidBackupFilePicker(fileCreateLauncher, fileSelectLauncher) }
}

private fun Uri?.toFilePickResult(): FilePickResult = when {
    this == null -> FilePickResult.Canceled
    else -> FilePickResult.Picked(PlatformFileAndroid(this))
}