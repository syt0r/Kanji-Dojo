package ua.syt0r.kanji.presentation.screen.screen.home.screen.practice_dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R


enum class DialogOption { SELECT, CREATE }

@Composable
fun CreatePracticeOptionDialog(
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

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(vertical = 16.dp)
        ) {

            Text(
                text = stringResource(R.string.writing_dashboard_dialog_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

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

}