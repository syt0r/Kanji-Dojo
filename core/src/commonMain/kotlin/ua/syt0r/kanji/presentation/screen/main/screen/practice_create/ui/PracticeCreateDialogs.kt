package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.TinyCircularProgressBar
import ua.syt0r.kanji.presentation.common.ui.delayedState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract.ProcessingStatus

data class DeleteWritingPracticeDialogData(
    val practiceTitle: String,
    val currentAction: ProcessingStatus
)

@Composable
fun DeleteWritingPracticeDialog(
    state: State<DeleteWritingPracticeDialogData>,
    onDismissRequest: () -> Unit = {},
    onDeleteConfirmed: () -> Unit = {},
    onDeleteAnimationCompleted: () -> Unit = {},
) {

    val delayedState = delayedState(
        state = state,
        produceDelay = { old, _ -> 600L }
    )

    val action by remember { derivedStateOf { delayedState.value.currentAction } }

    if (action == ProcessingStatus.DeleteCompleted) {
        LaunchedEffect(Unit) {
            delay(600)
            onDeleteAnimationCompleted()
        }
    }

    val strings = resolveString { practiceCreate }

    MultiplatformDialog(
        onDismissRequest = { if (action == ProcessingStatus.Loaded) onDismissRequest() },
        title = { Text(text = strings.deleteTitle) },
        content = { Text(text = strings.deleteMessage(delayedState.value.practiceTitle)) },
        buttons = {
            TextButton(
                enabled = action == ProcessingStatus.Loaded,
                onClick = onDeleteConfirmed,
                modifier = Modifier.animateContentSize()
            ) {
                Text(
                    text = if (action == ProcessingStatus.DeleteCompleted) {
                        strings.deleteButtonCompleted
                    } else {
                        strings.deleteButtonDefault
                    }
                )
                if (action == ProcessingStatus.Deleting) {
                    TinyCircularProgressBar(
                        strokeWidth = 1.dp,
                        modifier = Modifier.padding(start = 4.dp).size(10.dp)
                    )
                }
            }
        }
    )

}

data class SaveWritingPracticeDialogData(
    val initialTitle: String?,
    val processingStatus: ProcessingStatus
)

@Composable
fun SaveWritingPracticeDialog(
    state: State<SaveWritingPracticeDialogData>,
    onInputSubmitted: (userInput: String) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    onSaveAnimationCompleted: () -> Unit = {}
) {

    val delayedState = delayedState(
        state = state,
        produceDelay = { old, new -> 600L }
    )

    val action = remember { derivedStateOf { delayedState.value.processingStatus } }
    if (action.value == ProcessingStatus.SaveCompleted) {
        LaunchedEffect(Unit) {
            delay(600)
            onSaveAnimationCompleted()
        }
    }

    val strings = resolveString { practiceCreate }

    var input: String by remember { mutableStateOf(delayedState.value.initialTitle ?: "") }

    val isDialogDissmisable = action.value == ProcessingStatus.Loaded

    MultiplatformDialog(
        onDismissRequest = { if (isDialogDissmisable) onDismissRequest() },
        title = { Text(text = strings.saveTitle) },
        content = {
            TextField(
                value = input,
                onValueChange = { input = it },
                singleLine = true,
                modifier = Modifier.clip(MaterialTheme.shapes.small)
                    .fillMaxWidth(),
                isError = input.isEmpty(),
                trailingIcon = {
                    IconButton(onClick = { input = "" }) {
                        Icon(Icons.Default.Close, null)
                    }
                },
                label = { Text(strings.saveInputHint) },
                enabled = action.value == ProcessingStatus.Loaded,
                colors = TextFieldDefaults.colors(
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    errorCursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                    errorLabelColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        buttons = {
            TextButton(
                enabled = when (action.value) {
                    ProcessingStatus.Loaded -> input.isNotEmpty()
                    else -> false
                },
                onClick = { onInputSubmitted(input) },
                modifier = Modifier.animateContentSize()
            ) {
                Text(
                    text = if (action.value == ProcessingStatus.SaveCompleted) {
                        strings.saveButtonCompleted
                    } else {
                        strings.saveButtonDefault
                    }
                )
                if (action.value == ProcessingStatus.Saving) {
                    TinyCircularProgressBar(
                        strokeWidth = 1.dp,
                        modifier = Modifier.padding(start = 4.dp).size(10.dp)
                    )
                }
            }
        }
    )

}

@Composable
fun PracticeCreateLeaveConfirmation(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {

    val strings = resolveString { practiceCreate }

    MultiplatformDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(strings.leaveConfirmationTitle) },
        content = { Text(strings.leaveConfirmationMessage) },
        buttons = {
            TextButton(onDismissRequest) {
                Text(strings.leaveConfirmationCancel)
            }
            TextButton(onConfirmation) {
                Text(strings.leaveConfirmationAccept)
            }
        }
    )

}

@Composable
fun PracticeCreateUnknownCharactersDialog(
    characters: List<String>,
    onDismissRequest: () -> Unit
) {

    MultiplatformDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = resolveString { practiceCreate.unknownTitle })
        },
        content = {
            Text(text = resolveString { practiceCreate.unknownMessage(characters.toList()) })
        },
        buttons = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = resolveString { practiceCreate.unknownButton })
            }
        }
    )

}
