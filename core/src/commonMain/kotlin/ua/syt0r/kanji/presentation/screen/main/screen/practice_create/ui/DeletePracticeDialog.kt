package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.TinyCircularProgressBar
import ua.syt0r.kanji.presentation.common.ui.delayedState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract.DataAction

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

    val action by remember { derivedStateOf { delayedState.value.currentAction } }

    if (action == DataAction.DeleteCompleted) {
        LaunchedEffect(Unit) {
            delay(600)
            onDeleteAnimationCompleted()
        }
    }

    MultiplatformDialog(
        onDismissRequest = { if (action == DataAction.Loaded) onDismissRequest() }
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
                text = resolveString { practiceCreate.deleteTitle },
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = resolveString {
                    practiceCreate.deleteMessage(delayedState.value.practiceTitle)
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            TextButton(
                enabled = action == DataAction.Loaded,
                onClick = onDeleteConfirmed,
                modifier = Modifier.animateContentSize().align(Alignment.End)
            ) {
                Text(
                    text = resolveString {
                        if (action == DataAction.DeleteCompleted) {
                            practiceCreate.deleteButtonCompleted
                        } else {
                            practiceCreate.deleteButtonDefault
                        }
                    }
                )
                if (action == DataAction.Deleting) {
                    TinyCircularProgressBar(
                        strokeWidth = 1.dp,
                        modifier = Modifier.padding(start = 4.dp).size(10.dp)
                    )
                }
            }

        }

    }

}

//@Preview
//@Composable
//private fun Preview() {
//    val coroutineScope = rememberCoroutineScope()
//    val state = remember {
//        mutableStateOf(
//            DeleteWritingPracticeDialogData(
//                practiceTitle = "Test",
//                currentAction = DataAction.Loaded
//            )
//        )
//    }
//
//    AppTheme {
//        DeleteWritingPracticeDialog(
//            state = state,
//            onDeleteConfirmed = {
//                coroutineScope.launch {
//                    state.value = DeleteWritingPracticeDialogData(
//                        practiceTitle = "Test",
//                        currentAction = DataAction.Deleting
//                    )
//                    delay(50)
//                    state.value = DeleteWritingPracticeDialogData(
//                        practiceTitle = "Test",
//                        currentAction = DataAction.DeleteCompleted
//                    )
//                }
//            }
//        )
//    }
//}