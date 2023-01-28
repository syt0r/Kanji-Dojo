package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.RoundedCircularProgressBar
import ua.syt0r.kanji.presentation.common.ui.delayedState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.CreateWritingPracticeScreenContract.DataAction

data class SaveWritingPracticeDialogData(
    val initialTitle: String?,
    val dataAction: DataAction
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveWritingPracticeDialog(
    state: State<SaveWritingPracticeDialogData>,
    onInputSubmitted: (userInput: String) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    onSaveAnimationCompleted: () -> Unit = {}
) {

    val delayedState = delayedState(
        state = state,
        produceDelay = { old, new -> if (old.dataAction == DataAction.Loaded) 0L else 600L }
    )

    val action = remember { derivedStateOf { delayedState.value.dataAction } }
    if (action.value == DataAction.SaveCompleted) {
        LaunchedEffect(Unit) {
            delay(600)
            onSaveAnimationCompleted()
        }
    }

    var input: String by remember { mutableStateOf(delayedState.value.initialTitle ?: "") }

    val isDialogDissmisable = action.value == DataAction.Loaded

    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = isDialogDissmisable,
            dismissOnBackPress = isDialogDissmisable
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        title = {
            Text(text = stringResource(R.string.practice_create_save_dialog_title))
        },
        text = {
            TextField(
                value = input,
                onValueChange = { input = it },
                singleLine = true,
                modifier = Modifier.clip(MaterialTheme.shapes.small),
                isError = input.isEmpty(),
                trailingIcon = {
                    IconButton(onClick = { input = "" }) {
                        Icon(Icons.Default.Close, null)
                    }
                },
                label = { Text(stringResource(R.string.practice_create_save_dialog_input_hint)) },
                enabled = action.value == DataAction.Loaded,
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    errorCursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                    errorLabelColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        confirmButton = {
            TextButton(
                enabled = when (action.value) {
                    DataAction.Loaded -> input.isNotEmpty()
                    else -> false
                },
                onClick = { onInputSubmitted(input) },
                modifier = Modifier.animateContentSize()
            ) {
                Text(
                    text = if (action.value == DataAction.SaveCompleted) {
                        stringResource(R.string.practice_create_save_dialog_save_button_completed)
                    } else {
                        stringResource(R.string.practice_create_save_dialog_save_button_default)
                    }
                )
                if (action.value == DataAction.Saving) {
                    RoundedCircularProgressBar(
                        strokeWidth = 1.dp, Modifier
                            .padding(start = 4.dp)
                            .size(10.dp)
                    )
                }
            }
        }
    )

}

@Preview
@Composable
private fun Preview() {
    val coroutineScope = rememberCoroutineScope()
    val state = remember {
        mutableStateOf(
            SaveWritingPracticeDialogData(
                initialTitle = "Test",
                dataAction = DataAction.Loaded
            )
        )
    }

    AppTheme {
        SaveWritingPracticeDialog(
            state = state,
            onInputSubmitted = {
                coroutineScope.launch {
                    state.value = SaveWritingPracticeDialogData(
                        initialTitle = "Test",
                        dataAction = DataAction.Saving
                    )
                    delay(50)
                    state.value = SaveWritingPracticeDialogData(
                        initialTitle = "Test",
                        dataAction = DataAction.SaveCompleted
                    )
                }
            }
        )
    }
}