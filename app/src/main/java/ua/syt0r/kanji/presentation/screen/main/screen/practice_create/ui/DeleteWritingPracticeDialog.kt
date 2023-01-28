package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

data class DeleteWritingPracticeDialogData(
    val practiceTitle: String,
    val currentAction: DataAction
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
        produceDelay = { old, _ ->
            if (old.currentAction == DataAction.Loaded) 0L
            else 600L
        }
    )

    val isDeleteCompleted = remember {
        derivedStateOf { delayedState.value.currentAction == DataAction.DeleteCompleted }
    }

    if (isDeleteCompleted.value) {
        LaunchedEffect(Unit) {
            delay(600)
            onDeleteAnimationCompleted()
        }
    }

    val isIdle = remember {
        derivedStateOf { delayedState.value.currentAction == DataAction.Loaded }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = isIdle.value,
            dismissOnBackPress = isIdle.value
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        title = {
            Text(text = stringResource(R.string.practice_create_delete_dialog_title))
        },
        text = {
            Text(
                text = stringResource(
                    R.string.practice_create_delete_dialog_message,
                    delayedState.value.practiceTitle
                )
            )
        },
        confirmButton = {
            TextButton(
                enabled = isIdle.value,
                onClick = onDeleteConfirmed,
                modifier = Modifier.animateContentSize(),
            ) {
                Text(
                    text = if (isDeleteCompleted.value) {
                        stringResource(R.string.practice_create_delete_dialog_button_completed)
                    } else {
                        stringResource(R.string.practice_create_delete_dialog_button_default)
                    }
                )
                AnimatedVisibility(visible = delayedState.value.currentAction == DataAction.Deleting) {
                    RoundedCircularProgressBar(
                        strokeWidth = 1.dp,
                        Modifier
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
            DeleteWritingPracticeDialogData(
                practiceTitle = "Test",
                currentAction = DataAction.Loaded
            )
        )
    }

    AppTheme {
        DeleteWritingPracticeDialog(
            state = state,
            onDeleteConfirmed = {
                coroutineScope.launch {
                    state.value = DeleteWritingPracticeDialogData(
                        practiceTitle = "Test",
                        currentAction = DataAction.Deleting
                    )
                    delay(50)
                    state.value = DeleteWritingPracticeDialogData(
                        practiceTitle = "Test",
                        currentAction = DataAction.DeleteCompleted
                    )
                }
            }
        )
    }
}