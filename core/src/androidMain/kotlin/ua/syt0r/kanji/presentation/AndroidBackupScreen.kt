package ua.syt0r.kanji.presentation

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.core.module.Module
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.backup.BackupManager
import ua.syt0r.kanji.core.file.PlatformFile
import ua.syt0r.kanji.core.toLocalDateTime
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.backup.BackupMimeType
import ua.syt0r.kanji.presentation.screen.main.screen.backup.BackupScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.backup.BackupScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.backup.BackupScreenUI
import ua.syt0r.kanji.presentation.screen.main.screen.backup.FilePickResult
import ua.syt0r.kanji.presentation.screen.main.screen.backup.getDefaultBackupFileName

fun Module.backupScreenComponents() {

    single<BackupScreenContract.Content> { AndroidBackupScreenContent }

    multiplatformViewModel {
        AndroidBackupScreenViewModel(
            viewModelScope = it.component1(),
            backupManager = get(),
            analyticsManager = get()
        )
    }

}

object AndroidBackupScreenContent : BackupScreenContract.Content {

    @Composable
    override fun invoke(navigationState: MainNavigationState) {

        val viewModel = getMultiplatformViewModel<AndroidBackupScreenViewModel>()

        val filePicker = rememberBackupFilePicker(
            onFileCreateCallback = {
                if (it is FilePickResult.Picked) viewModel.createBackup(it.file)
            },
            onFileSelectCallback = {
                if (it is FilePickResult.Picked) viewModel.readBackupInfo(it.file)
            }
        )

        BackupScreenUI(
            state = viewModel.state.collectAsState(),
            onUpButtonClick = { navigationState.navigateBack() },
            createBackup = { filePicker.startCreateFileFlow() },
            readBackup = { filePicker.startSelectFileFlow() },
            restoreFromBackup = { viewModel.restoreFromBackup() }
        )

    }

    @Composable
    private fun rememberBackupFilePicker(
        onFileCreateCallback: ((FilePickResult) -> Unit)?,
        onFileSelectCallback: ((FilePickResult) -> Unit)?
    ): AndroidBackupFilePicker {
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

}

class AndroidBackupScreenViewModel(
    private val viewModelScope: CoroutineScope,
    private val backupManager: BackupManager,
    private val analyticsManager: AnalyticsManager
) {

    companion object {
        private const val UNKNOWN_ERROR_MESSAGE = "Unknown error"
    }

    private val screenStateFlow = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val state: StateFlow<ScreenState> = screenStateFlow

    fun createBackup(file: PlatformFile) {
        screenStateFlow.value = ScreenState.UninterruptibleLoading

        viewModelScope.launch {

            screenStateFlow.value = runCatching {
                backupManager.backupTo(file)
                analyticsManager.sendEvent("backup_created")
                ScreenState.ActionCompleted
            }.getOrElse {
                analyticsManager.sendEvent("backup_create_failed") {
                    put("message", it.message ?: UNKNOWN_ERROR_MESSAGE)
                }
                ScreenState.Error(it.message)
            }
        }
    }

    fun readBackupInfo(file: PlatformFile) {
        screenStateFlow.value = ScreenState.Loading

        viewModelScope.launch {
            screenStateFlow.value = runCatching {
                val backupInfo = backupManager.readInfoFrom(file)

                val currentDbVersion = UserDataDatabase.Schema.version
                if (backupInfo.databaseVersion > currentDbVersion)
                    throw IllegalStateException("Can't import database with newer schema (${backupInfo.databaseVersion}) than current ($currentDbVersion)")

                ScreenState.RestoreConfirmation(
                    file = file,
                    currentDbVersion = currentDbVersion,
                    backupDbVersion = backupInfo.databaseVersion,
                    backupCreateTimestamp = Instant
                        .fromEpochMilliseconds(backupInfo.backupCreateTimestamp)
                        .toLocalDateTime()
                )
            }.getOrElse {
                ScreenState.Error(it.message)
            }
        }
    }

    fun restoreFromBackup() {
        val currentScreenState = screenStateFlow.value as? ScreenState.RestoreConfirmation
            ?: return

        screenStateFlow.value = ScreenState.UninterruptibleLoading
        viewModelScope.launch {
            screenStateFlow.value = runCatching {
                backupManager.restoreFrom(currentScreenState.file)
                analyticsManager.sendEvent("restore_completed")
                ScreenState.ActionCompleted
            }.getOrElse {
                analyticsManager.sendEvent("restore_failed") {
                    put("message", it.message ?: UNKNOWN_ERROR_MESSAGE)
                }
                ScreenState.Error(it.message)
            }
        }
    }

}

private class AndroidBackupFilePicker(
    private val create: ManagedActivityResultLauncher<String, Uri?>?,
    private val select: ManagedActivityResultLauncher<Array<String>, Uri?>?
) {

    fun startCreateFileFlow() {
        create!!.launch(getDefaultBackupFileName())
    }

    fun startSelectFileFlow() {
        select!!.launch(arrayOf(BackupMimeType))
    }

}

private fun Uri?.toFilePickResult(): FilePickResult = when {
    this == null -> FilePickResult.Canceled
    else -> FilePickResult.Picked(PlatformFile(this))
}
