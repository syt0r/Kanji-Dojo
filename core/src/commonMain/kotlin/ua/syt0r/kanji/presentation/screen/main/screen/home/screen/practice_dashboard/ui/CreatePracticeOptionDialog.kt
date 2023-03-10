package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.resources.string.resolveString


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

    MultiplatformDialog(
        onDismissRequest = onDismiss
    ) {

        Column(
            modifier = Modifier.padding(bottom = 18.dp)
        ) {

            Text(
                text = resolveString { createPracticeDialog.title },
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
            )

            Text(
                text = resolveString { createPracticeDialog.selectMessage },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { delayedClick(DialogOption.SELECT) })
                    .padding(vertical = 16.dp, horizontal = 24.dp)
            )

            Text(
                text = resolveString { createPracticeDialog.createMessage },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { delayedClick(DialogOption.CREATE) })
                    .padding(vertical = 16.dp, horizontal = 24.dp)
            )

        }
    }

}

//@Preview
//@Composable
//private fun Preview() {
//    AppTheme {
//        CreatePracticeOptionDialog()
//    }
//}