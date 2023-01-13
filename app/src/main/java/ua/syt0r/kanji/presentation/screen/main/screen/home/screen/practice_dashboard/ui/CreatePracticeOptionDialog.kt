package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme


enum class DialogOption { SELECT, CREATE }

@Composable
fun CreatePracticeOptionDialog(
    onDismiss: () -> Unit = {},
    onOptionSelected: (DialogOption) -> Unit = {}
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
                .padding(bottom = 18.dp)
        ) {

            Text(
                text = stringResource(R.string.practice_dashboard_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.practice_dashboard_dialog_import_option),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { delayedClick(DialogOption.SELECT) })
                    .padding(vertical = 16.dp, horizontal = 24.dp)
            )

            Text(
                text = stringResource(R.string.practice_dashboard_dialog_custom_option),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { delayedClick(DialogOption.CREATE) })
                    .padding(vertical = 16.dp, horizontal = 24.dp)
            )

        }
    }

}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        CreatePracticeOptionDialog()
    }
}