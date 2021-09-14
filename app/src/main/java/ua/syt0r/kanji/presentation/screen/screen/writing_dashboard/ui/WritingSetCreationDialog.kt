package ua.syt0r.kanji.presentation.screen.screen.writing_dashboard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R


enum class DialogOption { IMPORT, CUSTOM }

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

    Dialog(onDismissRequest = onDismiss) {

        Card(
            modifier = Modifier.width(280.dp),
            shape = RoundedCornerShape(corner = CornerSize(size = 16.dp))
        ) {

            Column {

                Text(
                    text = stringResource(R.string.writing_dashboard_dialog_title),
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp),
                    style = MaterialTheme.typography.h6
                )

                Spacer(modifier = Modifier.size(width = 0.dp, height = 12.dp))

                Text(
                    text = stringResource(R.string.writing_dashboard_dialog_import_option),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { delayedClick(DialogOption.IMPORT) })
                        .padding(vertical = 12.dp, horizontal = 20.dp)
                )

                Text(
                    text = stringResource(R.string.writing_dashboard_dialog_custom_option),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { delayedClick(DialogOption.CUSTOM) })
                        .padding(vertical = 12.dp, horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.size(width = 0.dp, height = 12.dp))

            }

        }

    }

}