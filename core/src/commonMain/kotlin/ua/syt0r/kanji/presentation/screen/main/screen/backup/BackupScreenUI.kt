package ua.syt0r.kanji.presentation.screen.main.screen.backup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ua.syt0r.kanji.core.backup.PlatformFile
import ua.syt0r.kanji.core.logger.Logger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreenUI(
    onUpButtonClick: () -> Unit,
    createBackup: (location: PlatformFile) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup & Restore") },
                navigationIcon = {
                    IconButton(onClick = onUpButtonClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {

            val filePicker = rememberBackupFilePicker(
                onFileCreateCallback = { if (it is FilePickResult.Picked) createBackup(it.file) },
                onFileSelectCallback = { Logger.d("select=$it") }
            )

            TextButton(
                onClick = { filePicker.startCreateFileFlow() }
            ) {
                Text("Create backup")
            }

            TextButton(
                onClick = { filePicker.startSelectFileFlow() }
            ) {
                Text("Restore from backup")
            }

        }

    }

}
