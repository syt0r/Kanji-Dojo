package ua.syt0r.kanji.presentation.screen.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.user_data.database.DatabaseMigrationState
import ua.syt0r.kanji.presentation.common.MultiplatformDialog

@Composable
fun MigrationDialog(
    state: State<DatabaseMigrationState>
) {

    val currentState = state.value
    if (currentState == DatabaseMigrationState.Idle) return

    MultiplatformDialog(
        onDismissRequest = {},
        title = { Text("Finalizing Update") },
        content = {
            when (currentState) {
                DatabaseMigrationState.Idle -> {
                    // No-op
                }

                is DatabaseMigrationState.Running -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.animateContentSize()
                    ) {
                        Text(currentState.message)
                        val progress = currentState.progress
                        if (progress != null && progress.total > 0) {
                            LinearProgressIndicator(
                                progress = { progress.run { current.toFloat() / total } },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        buttons = {}
    )

}