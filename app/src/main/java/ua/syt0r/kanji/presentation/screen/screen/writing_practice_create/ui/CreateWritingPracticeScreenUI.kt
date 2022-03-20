package ua.syt0r.kanji.presentation.screen.screen.writing_practice_create.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_create.CreateWritingPracticeScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_create.CreateWritingPracticeScreenContract.StateType
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_create.data.EnteredKanji
import kotlin.random.Random

private const val KANJI_IN_ROW = 6

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWritingPracticeScreenUI(
    state: State,
    onUpClick: () -> Unit,
    submitKanjiInput: (input: String) -> Unit,
    createPractice: (title: String) -> Unit
) {

    var shouldShowTitleInputDialog by remember { mutableStateOf(false) }

    if (shouldShowTitleInputDialog) {
        TitleInputDialog(
            onInputSubmitted = {
                createPractice(it)
                shouldShowTitleInputDialog = false
            },
            onCancel = { shouldShowTitleInputDialog = false }
        )
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(stringResource(R.string.writing_practice_create_title)) },
                navigationIcon = {
                    IconButton(onClick = onUpClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(
                state = state,
                onCreateButtonClick = { shouldShowTitleInputDialog = true }
            )
        }
    ) {

        when (state.stateType) {
            StateType.Loading,
            StateType.Saving -> {
                LoadingState()
            }
            StateType.Loaded,
            StateType.Done -> {
                // No-op
            }
        }

        ScreenContent(
            state,
            onUserSubmittedInput = { submitKanjiInput(it) },
            onKanjiClick = { TODO() },
            bottomPadding = it.calculateBottomPadding()
        )

    }

}

@Composable
private fun ScreenContent(
    state: State,
    onUserSubmittedInput: (String) -> Unit,
    onKanjiClick: (String) -> Unit,
    bottomPadding: Dp
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        val enteredText = remember { mutableStateOf("") }
        val textColor = LocalContentColor.current.copy(LocalContentAlpha.current)

        OutlinedTextField(
            value = enteredText.value,
            onValueChange = { enteredText.value = it },
            singleLine = true,
            label = { androidx.compose.material.Text("Enter kanji here") },
            shape = CircleShape,
            leadingIcon = {
                IconButton(
                    onClick = { enteredText.value = "" }
                ) {
                    Icon(Icons.Default.Close, null)
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onUserSubmittedInput(enteredText.value)
                        enteredText.value = ""
                    }
                ) {
                    Icon(Icons.Default.Search, null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedTextFieldColors(
                textColor = textColor,
                unfocusedBorderColor = textColor,
                focusedBorderColor = textColor,
                cursorColor = textColor,
                unfocusedLabelColor = textColor,
                focusedLabelColor = textColor
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {

            items(state.data.chunked(KANJI_IN_ROW)) {
                KanjiRow(kanjiInRow = it, onKanjiClick = onKanjiClick)
            }

            item {
                Spacer(modifier = Modifier.height(bottomPadding + 8.dp))
            }

        }

    }

}

@Composable
private fun BottomBar(
    state: State,
    onCreateButtonClick: () -> Unit
) {

    Box(modifier = Modifier.fillMaxWidth()) {

        val isEnabled = state.run {
            stateType == StateType.Loaded && state.data.isNotEmpty()
        }

        val listSize = state.data.size

        OutlinedButton(
            onClick = onCreateButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 12.dp),
            enabled = isEnabled
        ) {

            Text("Create (${listSize} items)")

        }
    }
}

@Composable
private fun LoadingState() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {

        CircularProgressIndicator()

    }
}

@Composable
private fun KanjiRow(
    kanjiInRow: List<EnteredKanji>,
    onKanjiClick: (String) -> Unit,
) {

    Row(modifier = Modifier.padding(horizontal = 2.dp)) {

        kanjiInRow.forEach {

            Text(
                text = it.kanji,
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onKanjiClick(it.kanji) }
                    .wrapContentSize(),
                fontSize = 32.sp
            )

        }

        if (kanjiInRow.size != KANJI_IN_ROW) {
            Box(modifier = Modifier.weight(KANJI_IN_ROW - kanjiInRow.size.toFloat()))
        }

    }

}

@Composable
private fun TitleInputDialog(
    onInputSubmitted: (userInput: String) -> Unit,
    onCancel: () -> Unit
) {

    var input: String by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = { onInputSubmitted(input) }) {
                Text(text = "Create")
            }
        },
        dismissButton = {},
        title = {
            Text(text = "Name")
        },
        text = {
            TextField(value = input, onValueChange = { input = it })
        }
    )

}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        CreateWritingPracticeScreenUI(
            state = State(
                data = (0..10)
                    .map {
                        EnteredKanji(
                            kanji = Char(
                                Random.nextInt(Char.MIN_VALUE.code, Char.MAX_VALUE.code)
                            ).toString(),
                            isKnown = Random.nextBoolean()
                        )
                    }
                    .toSet(),
                stateType = StateType.Loaded
            ),
            onUpClick = { },
            submitKanjiInput = {},
            createPractice = {}
        )
    }

}