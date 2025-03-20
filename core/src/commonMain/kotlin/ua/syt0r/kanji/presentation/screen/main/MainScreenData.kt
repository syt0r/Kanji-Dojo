package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import kotlinx.datetime.LocalDateTime
import ua.syt0r.kanji.core.ApiRequestIssue
import ua.syt0r.kanji.core.sync.SyncDataDiffType

data class MainSnackbarNotification(
    override val message: String,
    val isError: Boolean,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = true,
    override val duration: SnackbarDuration = SnackbarDuration.Long,
    val handleAction: () -> MainDestination?
) : SnackbarVisuals

sealed interface SyncDialogState {

    data object Hidden : SyncDialogState

    data object Uploading : SyncDialogState
    data object Downloading : SyncDialogState

    data class Conflict(
        val diffType: SyncDataDiffType,
        val remoteDataTime: LocalDateTime?,
        val lastSyncTime: LocalDateTime?
    ) : SyncDialogState

    sealed interface Error : SyncDialogState {
        data object Unsupported : Error
        data class Api(val issue: ApiRequestIssue) : Error
    }

}