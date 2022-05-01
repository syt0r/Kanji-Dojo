package ua.syt0r.kanji.presentation.screen.screen.kanji_info.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.language.CharactersClassification
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.kanji.AnimatedKanji
import ua.syt0r.kanji.presentation.common.ui.kanji.KanjiBackground
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.KanjiInfoScreenContract.ScreenState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanjiInfoScreenUI(
    char: String,
    screenState: ScreenState,
    onUpButtonClick: () -> Unit = {},
    onCopyButtonClick: () -> Unit = {}
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = char) },
                navigationIcon = {
                    IconButton(onClick = onUpButtonClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {

        Crossfade(
            targetState = screenState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) { screenState ->

            when (screenState) {

                ScreenState.Loading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ScreenState.Loaded -> {
                    when (screenState) {
                        is ScreenState.Loaded.Kana -> {
                            KanaInfo(
                                screenState = screenState,
                                onCopyButtonClick = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Copied",
                                            withDismissAction = true
                                        )
                                    }
                                }
                            )
                        }
                        is ScreenState.Loaded.Kanji -> {
                            KanjiInfo(
                                screenState = screenState,
                                onCopyButtonClick = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Copied",
                                            withDismissAction = true
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KanaInfo(
    screenState: ScreenState.Loaded.Kana,
    onCopyButtonClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        Row {

            Column {

                Card(
                    modifier = Modifier
                        .size(120.dp),
                    elevation = CardDefaults.elevatedCardElevation()
                ) {

                    Box(modifier = Modifier.fillMaxSize()) {

                        KanjiBackground(Modifier.fillMaxSize())

                        AnimatedKanji(
                            strokes = screenState.strokes,
                            modifier = Modifier.fillMaxSize()
                        )

                    }

                }

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(screenState.strokes.size.toString())
                        }
                        append(" strokes")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.CenterHorizontally)
                )

            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {

                Text(
                    text = screenState.kanaSystem,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = "Romaji: ${screenState.reading}",
                    style = MaterialTheme.typography.titleSmall
                )

                OutlinedIconButton(
                    onClick = onCopyButtonClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(painterResource(R.drawable.ic_baseline_content_copy_24), null)
                }

            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KanjiInfo(
    screenState: ScreenState.Loaded.Kanji,
    onCopyButtonClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        Row {

            Column {

                Card(
                    modifier = Modifier
                        .size(120.dp),
                    elevation = CardDefaults.elevatedCardElevation()
                ) {

                    Box(modifier = Modifier.fillMaxSize()) {

                        KanjiBackground(Modifier.fillMaxSize())

                        AnimatedKanji(
                            strokes = screenState.strokes,
                            modifier = Modifier.fillMaxSize()
                        )

                    }

                }

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(screenState.strokes.size.toString())
                        }
                        append(" strokes")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.CenterHorizontally)
                )

            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {

                Text(
                    text = """
                    Jōyō kanji, taught in grade 2
                    JLPT level N4
                    133 of 2500 most used kanji in newspapers
                """.trimIndent(),
                    style = MaterialTheme.typography.titleSmall
                )

                OutlinedIconButton(
                    onClick = onCopyButtonClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(painterResource(R.drawable.ic_baseline_content_copy_24), null)
                }

            }

        }

        Spacer(modifier = Modifier.size(16.dp))

        Row {

            AutoBreakRow(Modifier.weight(1f)) {

                screenState.meanings.forEach {
                    Text(
                        text = it,
                        modifier = Modifier
                            .alignByBaseline()
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                    )
                }

            }

        }

        Spacer(modifier = Modifier.size(16.dp))

        if (screenState.kun.isNotEmpty())
            Row {
                Text(text = "Kun")
                AutoBreakRow(Modifier.weight(1f)) {
                    screenState.kun.forEach { Text(text = it) }
                }
            }

        if (screenState.on.isNotEmpty())
            Row {
                Text(text = "On")
                AutoBreakRow(Modifier.weight(1f)) {
                    screenState.on.forEach { Text(text = it) }
                }
            }

        Spacer(modifier = Modifier.size(16.dp))

    }

}

@Preview
@Composable
private fun KanaPreview() {

    AppTheme {
        KanjiInfoScreenUI(
            char = PreviewKanji.kanji,
            screenState = ScreenState.Loaded.Kana(
                character = "あ",
                kanaSystem = "Hiragana",
                strokes = PreviewKanji.strokes,
                reading = "A"
            )
        )
    }

}

@Preview
@Composable
private fun KanjiPreview() {

    AppTheme {
        KanjiInfoScreenUI(
            char = PreviewKanji.kanji,
            screenState = ScreenState.Loaded.Kanji(
                kanji = PreviewKanji.kanji,
                strokes = PreviewKanji.strokes,
                meanings = PreviewKanji.meanings,
                on = PreviewKanji.on,
                kun = PreviewKanji.kun,
                jlptLevel = CharactersClassification.JLPT.N5
            )
        )
    }

}
