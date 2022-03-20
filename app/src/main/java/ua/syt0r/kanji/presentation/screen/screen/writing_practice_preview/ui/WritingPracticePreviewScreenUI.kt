package ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview.WritingPracticePreviewScreenContract.State
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingPracticePreviewScreenUI(
    practiceName: String,
    state: State,
    onUpButtonClick: () -> Unit,
    onPracticeStart: (PracticeConfiguration) -> Unit,
    onKanjiClicked: (String) -> Unit
) {

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = practiceName) },
                navigationIcon = {
                    IconButton(onClick = onUpButtonClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        bottomBar = {

            val selectedTabIndex = 0

            Column {

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    indicator = @Composable { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color.White
                        )
                    }
                ) {

                    listOf(
                        R.drawable.ic_baseline_view_list_24,
                        R.drawable.ic_baseline_settings_24
                    ).forEach {
                        Icon(
                            painter = painterResource(it),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp)
                        )
                    }
                }

            }

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

                    KanjiListItem(
                        kanji = kanji,
                        onClick = { onKanjiClicked(kanji) }
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

@Composable
private fun RowScope.KanjiListItem(
    kanji: String,
    onClick: () -> Unit
) {

    Text(
        text = kanji,
        modifier = Modifier
            .clickable { onClick() }
            .weight(1f)
            .height(50.dp)
            .wrapContentSize(),
        fontSize = 36.sp
    )

}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        WritingPracticePreviewScreenUI(
            practiceName = "Test Practice",
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