package ua.syt0r.kanji.presentation.screen.screen.kanji_info.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.CustomTopBar
import ua.syt0r.kanji.presentation.common.ui.kanji.Kanji
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.KanjiInfoScreenContract.State
import ua.syt0r.kanji_db_model.model.KanjiClassifications

@Composable
fun KanjiInfoScreenUI(
    kanji: String,
    state: State,
    onUpButtonClick: () -> Unit
) {

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.kanji_info_title, kanji),
                upButtonVisible = true,
                onUpButtonClick = onUpButtonClick
            )
        }
    ) {

        when (state) {

            State.Init,
            State.Loading -> {
                CircularProgressIndicator()
            }

            is State.Loaded -> {
                LoadedState(state)
            }

        }

    }

}

@Composable
private fun LoadedState(state: State.Loaded) {
    Column {
        Text(
            text = state.kanji,
            fontSize = 64.sp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
        )

        if (state.kun.isNotEmpty())
            DataRow {
                KanjiInfoSection(title = "Kun", dataList = state.kun)
            }

        if (state.on.isNotEmpty())
            DataRow {
                KanjiInfoSection(title = "On", dataList = state.on)
            }

        if (state.meanings.isNotEmpty())
            DataRow {
                KanjiInfoSection(title = "Meanings", dataList = state.meanings)
            }

        DataRow {
            Text(
                text = "JLPT Level: ${state.jlptLevel.toString()}",
                style = MaterialTheme.typography.h6
            )
        }

        DataRow {
            Text(
                text = "Strokes count: ${state.strokes.size}",
                style = MaterialTheme.typography.h6
            )
        }

        DataRow {
            Text(
                text = "Stroke order",
                style = MaterialTheme.typography.h6
            )
        }

        LazyRow {

            item {
                Spacer(modifier = Modifier.width(24.dp))
            }

            items(state.strokes.size) { index ->

                Row {
                    Card {
                        Kanji(
                            strokes = state.strokes.take(index + 1),
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }

            }

            item {
                Spacer(modifier = Modifier.width(24.dp))
            }

        }

    }

}

@Composable
private fun DataRow(
    horizontalPadding: Dp = 24.dp,
    content: @Composable RowScope.() -> Unit
) {

    Row(
        modifier = Modifier
            .defaultMinSize(minHeight = 24.dp)
            .padding(horizontal = horizontalPadding)
    ) {
        content()
    }

}

@Composable
private fun KanjiInfoSection(
    title: String,
    dataList: List<String>
) {

    Row {

        AutoBreakRow(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(top = 4.dp)
            )

            dataList.forEach {

                Text(
                    text = it,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colors.secondaryVariant,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    maxLines = 1
                )

            }

        }
    }

}

@Preview
@Composable
private fun Preview() {

    AppTheme {
        KanjiInfoScreenUI(
            kanji = "書",
            state = State.Loaded(
                kanji = PreviewKanji.kanji,
                strokes = PreviewKanji.strokes,
                meanings = PreviewKanji.meanings,
                on = PreviewKanji.on,
                kun = PreviewKanji.kun,
                jlptLevel = KanjiClassifications.JLPT.N5
            ),
            onUpButtonClick = {}
        )
    }

}
