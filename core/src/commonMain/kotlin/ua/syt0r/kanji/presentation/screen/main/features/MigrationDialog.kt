package ua.syt0r.kanji.presentation.screen.main.features

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.user_data.database.DatabaseMigrationState
import ua.syt0r.kanji.migration_dialog_title
import ua.syt0r.kanji.presentation.common.MultiplatformDialog

@Composable
fun MigrationDialog(
    currentState: DatabaseMigrationState.Running
) {

    MultiplatformDialog(
        onDismissRequest = {},
        title = { Text(stringResource(Res.string.migration_dialog_title)) },
        content = {
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
        },
        buttons = {}
    )

}