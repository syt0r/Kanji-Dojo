package ua.syt0r.kanji.ui.screen.screen.create_custom_set

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import ua.syt0r.kanji.R
import ua.syt0r.kanji.ui.screen.LocalMainNavigator
import ua.syt0r.kanji.ui.screen.MainContract
import ua.syt0r.kanji.ui.screen.screen.create_custom_set.CreateCustomSetScreenContract.*
import java.util.*

private const val KANJI_IN_ROW = 7

@Composable
fun CreateCustomPracticeSetScreen(
    viewModel: ViewModel = hiltViewModel<CreateCustomSetViewModel>(),
    mainNavigation: MainContract.Navigation = LocalMainNavigator.current
) {

    val state = viewModel.state.observeAsState()

    Scaffold(
        topBar = { TopBar { mainNavigation.navigateBack() } },
        bottomBar = {
            BottomBar(
                state = state.value!!,
                onButtonClick = { viewModel.createSet(UUID.randomUUID().toString()) }
            )
        }
    ) {

        Content(
            state = state.value!!,
            onUserSubmittedInput = { viewModel.submitUserInput(it) },
            onKanjiClick = { mainNavigation.navigateToKanjiInfo(it) },
            onSaved = { mainNavigation.navigateBack() },
            bottomPadding = it.calculateBottomPadding()
        )

    }

}

@Composable
private fun Content(
    state: State,
    onUserSubmittedInput: (String) -> Unit,
    onKanjiClick: (String) -> Unit,
    onSaved: () -> Unit,
    bottomPadding: Dp
) {

    when (state.stateType) {
        StateType.Saving -> {

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
        StateType.Done -> {
            onSaved()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            modifier = Modifier.padding(12.dp)
        ) {

            val enteredText = remember { mutableStateOf("") }

            OutlinedTextField(
                value = enteredText.value,
                onValueChange = { enteredText.value = it },
                singleLine = true,
                label = { Text("Kanji will be extacted from entered text") },
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colors.onPrimary,
                    cursorColor = MaterialTheme.colors.onPrimary,
                    focusedLabelColor = MaterialTheme.colors.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colors.onPrimary
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_search_24),
                contentDescription = null,
                tint = MaterialTheme.colors.onSecondary,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.secondary)
                    .clickable {
                        onUserSubmittedInput(enteredText.value)
                        enteredText.value = ""
                    }
                    .padding(12.dp)
                    .size(24.dp)
            )

        }

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
private fun TopBar(onBackButtonClick: () -> Unit) {

    TopAppBar(
        title = { Text(text = "Create practice set") },
        navigationIcon = {
            IconButton(
                onClick = onBackButtonClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                    contentDescription = null
                )
            }
        }
    )

}

@Composable
private fun BottomBar(
    state: State,
    onButtonClick: () -> Unit
) {

    Box(modifier = Modifier.fillMaxWidth()) {

        val isEnabled = state.run {
            stateType == StateType.Loaded && state.data.isNotEmpty()
        }

        val listSize = state.data.size

        OutlinedButton(
            onClick = onButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp, vertical = 12.dp),
            enabled = isEnabled,
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.secondary
            )
        ) {

            Text("Create (${listSize} items)")

        }
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
                        color = MaterialTheme.colors.secondary,
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