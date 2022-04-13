package ua.syt0r.kanji.presentation.screen.screen.practice_preview.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeConfiguration
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticePreviewScreenUI(
    state: State,
    onUpButtonClick: () -> Unit,
    onPracticeStart: (PracticeConfiguration) -> Unit,
    onKanjiClicked: (String) -> Unit
) {

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onUpButtonClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val currentState = state as State.Loaded
                    onPracticeStart(
                        PracticeConfiguration(
                            practiceId = currentState.practiceId,
                            kanjiList = currentState.kanjiList
                        )
                    )
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_play_arrow_24),
                    contentDescription = null
                )
            }
        }
    ) {

        Column {

            when (state) {
                State.Init,
                State.Loading -> {

                }
                is State.Loaded -> {
                    LoadedState(
                        kanjiList = state.kanjiList,
                        onKanjiClicked = onKanjiClicked
                    )
                }
            }

        }

    }

}


private const val itemsInRow = 6

@Composable
private fun LoadedState(
    kanjiList: List<String>,
    onKanjiClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier) {

        items(kanjiList.chunked(itemsInRow)) {

            Row {

                it.forEach { kanji ->

                    Text(
                        text = kanji,
                        modifier = Modifier
                            .clickable { onKanjiClicked(kanji) }
                            .weight(1f)
                            .height(50.dp)
                            .wrapContentSize(),
                        fontSize = 36.sp
                    )

                }

                if (it.size < itemsInRow) {
                    val weight = itemsInRow.toFloat() - it.size
                    Spacer(
                        modifier = Modifier.weight(weight)
                    )
                }

            }

        }

    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        PracticePreviewScreenUI(
            state = State.Loaded(
                practiceId = Random.nextLong(),
                kanjiList = listOf("A", "B")
            ),
            onUpButtonClick = {},
            onPracticeStart = {},
            onKanjiClicked = {}
        )
    }

}