package ua.syt0r.kanji.presentation.screen.screen.home.screen.writing_dashboard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R


enum class DialogOption { SELECT, CREATE }

@Composable
fun WritingSetCreationDialog(
    onDismiss: () -> Unit,
    onOptionSelected: (DialogOption) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val delayedClick: (DialogOption) -> Unit = {
        coroutineScope.launch {
            delay(150)
            onOptionSelected(it)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = {
            Text(
                text = stringResource(R.string.writing_dashboard_dialog_title),
            )
        },
        text = {

            Column {

                Text(
                    text = stringResource(R.string.writing_dashboard_dialog_import_option),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { delayedClick(DialogOption.SELECT) })
                        .padding(vertical = 12.dp, horizontal = 20.dp)
                )

                Text(
                    text = stringResource(R.string.writing_dashboard_dialog_custom_option),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { delayedClick(DialogOption.CREATE) })
                        .padding(vertical = 12.dp, horizontal = 20.dp)
                )

            }
        }
    )

}