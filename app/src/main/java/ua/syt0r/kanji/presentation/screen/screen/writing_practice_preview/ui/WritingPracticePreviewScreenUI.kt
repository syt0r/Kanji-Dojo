package ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.KanjiDojoTheme
import ua.syt0r.kanji.presentation.common.theme.secondary
import ua.syt0r.kanji.presentation.common.ui.CustomTopBar
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview.WritingPracticePreviewScreenContract.State
import kotlin.random.Random

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
            CustomTopBar(
                title = practiceName,
                upButtonVisible = true,
                onUpButtonClick = onUpButtonClick
            )
        },
        bottomBar = {

            val selectedTabIndex = 0

            Column {

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    backgroundColor = secondary,
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
                    onPracticeStart(
                        PracticeConfiguration(
                            practiceId = (state as State.Loaded).practiceId
                        )
                    )
                },
                backgroundColor = Color.White,
                contentColor = secondary
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
    KanjiDojoTheme {
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


@Composable
private fun WritingPracticePreviewScreenUITest1(
    practiceName: String,
    state: State,
    onUpButtonClick: () -> Unit,
    onPracticeStart: (PracticeConfiguration) -> Unit
) {

    Scaffold(
        topBar = {
            CustomTopBar(
                title = practiceName,
                upButtonVisible = true,
                onUpButtonClick = onUpButtonClick
            )
        },
        bottomBar = {

            val selectedTabIndex = 0

            Column {

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    backgroundColor = secondary,
                    indicator = @Composable { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color.White
                        )
                    }
                ) {

                    listOf("Kanji List", "Setup").forEach {
                        Text(
                            text = it.uppercase(),
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .wrapContentSize(),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Start".uppercase(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(secondary)
                        .padding(vertical = 12.dp, horizontal = 56.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            onPracticeStart(
                                PracticeConfiguration(
                                    practiceId = (state as State.Loaded).practiceId
                                )
                            )
                        }
                        .padding(vertical = 12.dp)
                        .wrapContentSize(),

                    color = secondary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

            }

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                backgroundColor = Color.White,
                contentColor = secondary
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_menu_open_24),
                    contentDescription = null
                )
            }
        }
    ) {

        Column {

            when (state) {
                State.Init,
                State.Loading -> {
                    Box() {}
                }
                is State.Loaded -> {
                    LoadedState(
                        kanjiList = state.kanjiList,
                        onKanjiClicked = { }
                    )
                }
            }

        }

    }

}