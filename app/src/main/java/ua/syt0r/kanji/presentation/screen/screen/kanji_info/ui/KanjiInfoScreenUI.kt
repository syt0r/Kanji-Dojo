package ua.syt0r.kanji.presentation.screen.screen.kanji_info.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.kanji.AnimatedKanji
import ua.syt0r.kanji.presentation.common.ui.kanji.KanjiBackground
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.KanjiInfoScreenContract.State
import ua.syt0r.kanji_db_model.model.KanjiClassifications


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanjiInfoScreenUI(
    state: State,
    onUpButtonClick: () -> Unit
) {

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onUpButtonClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadedState(state: State.Loaded) {

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            SelectionContainer(Modifier.weight(1f)) {
                Text(text = state.kanji, fontSize = 80.sp, textAlign = TextAlign.Center)
            }

            Text(
                text = state.meanings.first().capitalize(Locale.current),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Center
            )

        }

        Spacer(modifier = Modifier.size(16.dp))

        if (state.meanings.size > 1) {
            DataRow {
                AutoBreakRow(Modifier.weight(1f)) {

                    state.meanings.drop(1).forEach {
                        Text(
                            text = it,
                            modifier = Modifier
                                .alignByBaseline()
                                .padding(horizontal = 2.dp, vertical = 2.dp)
                        )
                    }

                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        if (state.kun.isNotEmpty())
            DataRow {
                Text(text = "Kun")
                AutoBreakRow(Modifier.weight(1f)) {
                    state.kun.forEach { Text(text = it) }
                }
            }

        if (state.on.isNotEmpty())
            DataRow {
                Text(text = "On")
                AutoBreakRow(Modifier.weight(1f)) {
                    state.on.forEach { Text(text = it) }
                }
            }

        Spacer(modifier = Modifier.size(16.dp))

        DataRow {

            Card(
                modifier = Modifier
                    .size(120.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                elevation = CardDefaults.elevatedCardElevation()
            ) {

                Box(modifier = Modifier.fillMaxSize()) {

                    KanjiBackground(Modifier.fillMaxSize())

                    AnimatedKanji(
                        strokes = state.strokes,
                        modifier = Modifier.fillMaxSize()
                    )

                }

            }

            Spacer(Modifier.width(8.dp))

            Column(Modifier.weight(1f)) {

                Text(
                    text = "Strokes count: ${state.strokes.size}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "JLPT Level: ${state.jlptLevel.toString()}",
                    style = MaterialTheme.typography.titleMedium
                )

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
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            dataList.forEach {

                Text(
                    text = it,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
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
