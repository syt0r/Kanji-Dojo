package ua.syt0r.kanji.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.LocalUIViewController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.core.module.Module
import platform.Foundation.NSURL
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIModalPresentationFullScreen
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTTypeZIP
import platform.darwin.NSObject
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.backup.BackupManager
import ua.syt0r.kanji.core.emitWhenWithSubscribers
import ua.syt0r.kanji.core.file.PlatformFile
import ua.syt0r.kanji.core.file.PlatformFileHandler
import ua.syt0r.kanji.core.formattedIosFilePath
import ua.syt0r.kanji.core.getPrivateAppDataDirPath
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.toLocalDateTime
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.backup.BackupScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.backup.BackupScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.backup.BackupScreenUI
import ua.syt0r.kanji.presentation.screen.main.screen.backup.FilePickResult
import ua.syt0r.kanji.presentation.screen.main.screen.backup.getDefaultBackupFileName

fun Module.backupScreenComponents() {

    single<BackupScreenContract.Content> { IosBackupScreenContent }

    multiplatformViewModel {
        IosBackupScreenViewModel(
            viewModelScope = it.component1(),
            backupManager = get(),
            platformFileHandler = get(),
            analyticsManager = get()
        )
    }

}

object IosBackupScreenContent : BackupScreenContract.Content {

    @Composable
    override fun invoke(navigationState: MainNavigationState) {

        val viewModel = getMultiplatformViewModel<IosBackupScreenViewModel>()

        val filePicker = rememberBackupFilePicker(
            onFileCreateCallback = {
                if (it is FilePickResult.Picked) viewModel.notifyCopyFlowCompletion()
            },
            onFileSelectCallback = {
                if (it is FilePickResult.Picked) viewModel.readBackupInfo(it.file)
            }
        )

        LaunchedEffect(Unit) {
            viewModel.tmpBackupFilePathFlow.collect { filePicker.startCreateFileFlow(it) }
        }

        BackupScreenUI(
            state = viewModel.state.collectAsState(),
            onUpButtonClick = { navigationState.navigateBack() },
            createBackup = { viewModel.createTmpBackupCopy() },
            readBackup = { filePicker.startSelectFileFlow() },
            restoreFromBackup = { viewModel.restoreFromBackup() }
        )

    }

    @Composable
    private fun rememberBackupFilePicker(
        onFileCreateCallback: ((FilePickResult) -> Unit)?,
        onFileSelectCallback: ((FilePickResult) -> Unit)?
    ): IosBackupFilePicker {
        val uiViewController = LocalUIViewController.current
        return remember {
            IosBackupFilePicker(uiViewController, onFileCreateCallback, onFileSelectCallback)
        }
    }

}

class IosBackupScreenViewModel(
    private val viewModelScope: CoroutineScope,
    private val backupManager: BackupManager,
    private val platformFileHandler: PlatformFileHandler,
    private val analyticsManager: AnalyticsManager
) {

    companion object {
        private const val UNKNOWN_ERROR_MESSAGE = "Unknown error"
    }

    private val screenStateFlow = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val state: StateFlow<ScreenState> = screenStateFlow

    private val _tmpBackupFilePathFlow = MutableSharedFlow<String>()
    val tmpBackupFilePathFlow: SharedFlow<String> = _tmpBackupFilePathFlow

    private lateinit var tmpBackupFile: PlatformFile

    fun createTmpBackupCopy() {
        screenStateFlow.value = ScreenState.UninterruptibleLoading
        tmpBackupFile = PlatformFile(
            url = getPrivateAppDataDirPath() + "/" + getDefaultBackupFileName()
        )

        viewModelScope.launch {

            runCatching {
                backupManager.backupTo(tmpBackupFile)
                _tmpBackupFilePathFlow.emitWhenWithSubscribers(tmpBackupFile.url)
                analyticsManager.sendEvent("backup_created")
            }.getOrElse {
                analyticsManager.sendEvent("backup_create_failed") {
                    put("message", it.message ?: UNKNOWN_ERROR_MESSAGE)
                }
                screenStateFlow.value = ScreenState.Error(it.message)
            }
        }
    }

    fun notifyCopyFlowCompletion() {
        screenStateFlow.value = ScreenState.UninterruptibleLoading

        viewModelScope.launch {
            platformFileHandler.delete(tmpBackupFile)
            screenStateFlow.value = ScreenState.ActionCompleted
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


private class IosBackupFilePicker(
    private val uiViewController: UIViewController,
    private val onCreate: ((FilePickResult) -> Unit)?,
    private val onSelect: ((FilePickResult) -> Unit)?
) {

    private var fileToCopyPath: String? = null

    private lateinit var delegate: UIDocumentPickerDelegateProtocol

    init {
        Logger.d("new file picker instance")
    }

    fun startCreateFileFlow(tmpFilePath: String) {
        fileToCopyPath = tmpFilePath
        presentPicker()
    }

    fun startSelectFileFlow() {
        fileToCopyPath = null
        presentPicker()
    }

    private fun presentPicker() {
        val controller = fileToCopyPath?.let {
            UIDocumentPickerViewController(
                forExportingURLs = listOf(NSURL(fileURLWithPath = it)),
                asCopy = true
            )
        } ?: UIDocumentPickerViewController(
            forOpeningContentTypes = listOf(UTTypeZIP),
            asCopy = false
        )

        controller.allowsMultipleSelection = false
        controller.modalPresentationStyle = UIModalPresentationFullScreen
        delegate = createDelegate()
        controller.delegate = delegate

        uiViewController.presentViewController(
            viewControllerToPresent = controller,
            animated = true,
            completion = { Logger.d("picker completion") }
        )
    }

    fun createDelegate(): UIDocumentPickerDelegateProtocol {
        return object : NSObject(), UIDocumentPickerDelegateProtocol {

            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentAtURL: NSURL
            ) {
                Logger.d("documentPicker")
                val path = didPickDocumentAtURL.absoluteString!!.formattedIosFilePath()
                Logger.d("documentPicker path[$path]")
                val result = FilePickResult.Picked(PlatformFile(path))
                when {
                    fileToCopyPath != null -> onCreate!!(result)
                    else -> onSelect!!(result)
                }
            }

            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>
            ) {
                Logger.d("documentPicker multiple")
                documentPicker(controller, didPickDocumentsAtURLs.first() as NSURL)
            }

            override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                val result = FilePickResult.Canceled
                Logger.d("documentPickerWasCancelled")
                when {
                    fileToCopyPath != null -> onCreate!!(result)
                    else -> onSelect!!(result)
                }
            }

        }
    }

}