package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.TinyCircularProgressBar
import ua.syt0r.kanji.presentation.common.ui.delayedState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract.DataAction

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

    MultiplatformDialog(
        onDismissRequest = { if (isDialogDissmisable) onDismissRequest() }
    ) {
        Column(
            modifier = Modifier.padding(
                top = 20.dp,
                start = 20.dp,
                end = 20.dp,
                bottom = 10.dp
            )
        ) {

            Text(
                text = resolveString { practiceCreate.saveTitle },
                style = MaterialTheme.typography.titleLarge
            )

            TextField(
                value = input,
                onValueChange = { input = it },
                singleLine = true,
                modifier = Modifier.padding(vertical = 8.dp).clip(MaterialTheme.shapes.small),
                isError = input.isEmpty(),
                trailingIcon = {
                    IconButton(onClick = { input = "" }) {
                        Icon(Icons.Default.Close, null)
                    }
                },
                label = { Text(resolveString { practiceCreate.saveInputHint }) },
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

            TextButton(
                enabled = when (action.value) {
                    DataAction.Loaded -> input.isNotEmpty()
                    else -> false
                },
                onClick = { onInputSubmitted(input) },
                modifier = Modifier.animateContentSize().align(Alignment.End)
            ) {
                Text(
                    text = resolveString {
                        if (action.value == DataAction.SaveCompleted) {
                            practiceCreate.saveButtonCompleted
                        } else {
                            practiceCreate.saveButtonDefault
                        }
                    }
                )
                if (action.value == DataAction.Saving) {
                    TinyCircularProgressBar(
                        strokeWidth = 1.dp,
                        modifier = Modifier.padding(start = 4.dp).size(10.dp)
                    )
                }
            }
        }
    }

}
//
//@Preview
//@Composable
//private fun Preview() {
//    val coroutineScope = rememberCoroutineScope()
//    val state = remember {
//        mutableStateOf(
//            SaveWritingPracticeDialogData(
//                initialTitle = "Test",
//                dataAction = DataAction.Loaded
//            )
//        )
//    }
//
//    AppTheme {
//        SaveWritingPracticeDialog(
//            state = state,
//            onInputSubmitted = {
//                coroutineScope.launch {
//                    state.value = SaveWritingPracticeDialogData(
//                        initialTitle = "Test",
//                        dataAction = DataAction.Saving
//                    )
//                    delay(50)
//                    state.value = SaveWritingPracticeDialogData(
//                        initialTitle = "Test",
//                        dataAction = DataAction.SaveCompleted
//                    )
//                }
//            }
//        )
//    }
//}