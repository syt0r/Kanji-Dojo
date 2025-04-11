package ua.syt0r.kanji.presentation.screen.main.screen.backup

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDateTime
import ua.syt0r.kanji.core.file.PlatformFile
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState


interface BackupScreenContract {

    interface Content {

        @Composable
        operator fun invoke(navigationState: MainNavigationState)

    }

    sealed interface ScreenState {

        object Idle : ScreenState
        object Loading : ScreenState
        object UninterruptibleLoading : ScreenState

        data class Error(
            val message: String?
        ) : ScreenState

        data class RestoreConfirmation(
            val file: PlatformFile,
            val currentDbVersion: Long,
            val backupDbVersion: Long,
            val backupCreateTimestamp: LocalDateTime
        ) : ScreenState

        object ActionCompleted : ScreenState

    }

}