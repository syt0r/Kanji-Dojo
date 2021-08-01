package ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.presentation.common.theme.secondary
import ua.syt0r.kanji.presentation.common.theme.stylizedFontFamily
import ua.syt0r.kanji.presentation.common.ui.CustomTopBar
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeConfiguration
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview.WritingPracticePreviewScreenContract.State

@Composable
fun WritingPracticePreviewScreenUI(
    state: State,
    onPracticeStart: (PracticeConfiguration) -> Unit
) {

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "",
                backButtonEnabled = true,
                onBackButtonClick = {}
            )
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
                Text(
                    text = "始",
                    fontFamily = stylizedFontFamily,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) {

        Box(
            modifier = Modifier.padding(
                top = it.calculateTopPadding(),
                bottom = it.calculateBottomPadding()
            )
        ) {

            when (state) {
                State.Init,
                State.Loading -> {

                }
                is State.Loaded -> {
                    KanjiList(
                        kanjiList = state.kanjiList,
                        onKanjiClicked = { }
                    )
                }
            }


        }


    }

}


private const val itemsInRow = 6

@Composable
private fun KanjiList(
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