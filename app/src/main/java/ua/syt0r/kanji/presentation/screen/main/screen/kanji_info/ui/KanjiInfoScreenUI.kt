package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.common.db.entity.CharacterRadical
import ua.syt0r.kanji.core.kanji_data.data.buildFuriganaString
import ua.syt0r.kanji.presentation.common.stringResource
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.ClickableFuriganaText
import ua.syt0r.kanji.presentation.common.ui.kanji.AnimatedKanji
import ua.syt0r.kanji.presentation.common.ui.kanji.KanjiBackground
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.common.ui.kanji.RadicalKanji
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract.ScreenState


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun KanjiInfoScreenUI(
    char: String,
    state: State<ScreenState>,
    onUpButtonClick: () -> Unit,
    onCopyButtonClick: () -> Unit,
    onFuriganaItemClick: (String) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val listState = rememberLazyListState()
    val fabPosition = remember { mutableStateOf<LayoutCoordinates?>(null) }

    val shouldShowScrollButton = remember {
        derivedStateOf(policy = structuralEqualityPolicy()) {
            listState.firstVisibleItemIndex != 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SelectionContainer { Text(text = char) }
                },
                navigationIcon = {
                    IconButton(onClick = onUpButtonClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = shouldShowScrollButton.value,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier.onPlaced { fabPosition.value = it }
            ) {
                FloatingActionButton(
                    onClick = { coroutineScope.launch { listState.scrollToItem(0) } }
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = null)
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {

        Crossfade(
            targetState = state.value,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) { screenState ->

            when (screenState) {

                ScreenState.Loading -> {
                    LoadingState()
                }

                ScreenState.NoData -> {
                    Text(
                        text = stringResource(R.string.kanji_info_no_data_message),
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }

                is ScreenState.Loaded -> {
                    val snackbarMessage = stringResource(R.string.kanji_info_snackbar_message)
                    LoadedState(
                        screenState = screenState,
                        listState = listState,
                        fabPosition = fabPosition,
                        onCopyButtonClick = {
                            onCopyButtonClick()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    snackbarMessage,
                                    withDismissAction = true
                                )
                            }
                        },
                        onFuriganaItemClick = onFuriganaItemClick
                    )
                }

            }

        }

    }

}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LoadedState(
    screenState: ScreenState.Loaded,
    listState: LazyListState,
    fabPosition: State<LayoutCoordinates?>,
    onCopyButtonClick: () -> Unit,
    onFuriganaItemClick: (String) -> Unit
) {

    var radicalsExpanded by rememberSaveable { mutableStateOf(true) }
    var wordsExpanded by rememberSaveable { mutableStateOf(false) }

    val words = remember {
        screenState.words.mapIndexed { index, japaneseWord ->
            buildFuriganaString {
                append("${index + 1}. ")
                append(japaneseWord.furiganaString)
                append(" - ")
                append(japaneseWord.meanings.first())
            }
        }
    }

    LazyColumn(
        state = listState
    ) {

        item {
            when (screenState) {
                is ScreenState.Loaded.Kana -> {
                    KanaInfo(
                        screenState = screenState,
                        onCopyButtonClick = onCopyButtonClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 16.dp)
                    )
                }
                is ScreenState.Loaded.Kanji -> {
                    KanjiInfo(
                        screenState = screenState,
                        onCopyButtonClick = onCopyButtonClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 16.dp)
                    )
                }
            }
        }


        item {

            ExpandableSectionHeader(
                text = stringResource(
                    R.string.kanji_info_radicals_section_title,
                    screenState.radicals.size
                ),
                isExpanded = radicalsExpanded,
                toggleExpandedState = { radicalsExpanded = !radicalsExpanded }
            )

        }

        if (radicalsExpanded) {
            item {
                RadicalsSectionContent(
                    strokes = screenState.strokes,
                    radicals = screenState.radicals
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            ExpandableSectionHeader(
                text = stringResource(
                    R.string.kanji_info_words_section_title,
                    screenState.words.size
                ),
                isExpanded = wordsExpanded,
                toggleExpandedState = { wordsExpanded = !wordsExpanded }
            )
        }

        if (wordsExpanded) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(words) {
                SelectionContainer {
                    ClickableFuriganaText(
                        furiganaString = it,
                        onClick = onFuriganaItemClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                }
            }

            item {

                val configuration = LocalConfiguration.current
                val density = LocalDensity.current

                val bottomPadding = remember {
                    derivedStateOf {
                        val fabBounds = fabPosition.value?.boundsInRoot()
                            ?: return@derivedStateOf 0.dp

                        configuration.screenHeightDp.dp - (fabBounds.top / density.density).dp + 16.dp
                    }
                }
                Spacer(modifier = Modifier.height(bottomPadding.value))

            }

        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

    }

}

@Composable
private fun ExpandableSectionHeader(
    text: String,
    isExpanded: Boolean,
    toggleExpandedState: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = toggleExpandedState)
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        IconButton(onClick = toggleExpandedState) {
            val rotation by animateFloatAsState(if (isExpanded) 0f else 180f)
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                modifier = Modifier.graphicsLayer(rotationZ = rotation)
            )
        }
    }

}

@Composable
private fun RadicalsSectionContent(
    strokes: List<Path>,
    radicals: List<CharacterRadical>
) {

    Row(
        Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
    ) {

        Box(
            modifier = Modifier.size(120.dp)
        ) {

            RadicalKanji(
                strokes = strokes,
                radicals = radicals,
                modifier = Modifier.fillMaxSize()
            )

        }

        if (radicals.isEmpty()) {

            Text(
                text = stringResource(R.string.kanji_info_no_radicals),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .height(120.dp)
                    .weight(1f)
                    .wrapContentSize()
            )

        } else {
            AutoBreakRow(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {

                radicals.forEach {
                    Text(
                        text = it.radical,
                        fontSize = 32.sp,
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable {}
                            .padding(8.dp)
                            .width(IntrinsicSize.Min)
                            .aspectRatio(1f, true)
                            .wrapContentSize(unbounded = true)
                    )
                }
            }
        }

    }

}

@Composable
private fun KanaInfo(
    screenState: ScreenState.Loaded.Kana,
    onCopyButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier) {

        Row {

            AnimatableCharacter(screenState.strokes)

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {

                Text(
                    text = screenState.kanaSystem.stringResource(),
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = stringResource(
                        R.string.kanji_info_kana_romaji_template,
                        screenState.reading
                    ),
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

@Composable
private fun KanjiInfo(
    screenState: ScreenState.Loaded.Kanji,
    onCopyButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier) {

        Row {

            AnimatableCharacter(strokes = screenState.strokes)

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {

                screenState.grade?.let {
                    Text(
                        text = when {
                            it <= 6 -> stringResource(R.string.kanji_info_joyo_grade_template, it)
                            it == 8 -> stringResource(R.string.kanji_info_joyo_grade_high)
                            it >= 9 -> stringResource(R.string.kanji_info_joyo_grade_names)
                            else -> throw IllegalStateException("Unknown grade $it for kanji ${screenState.character}")
                        },
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                screenState.jlpt?.let {
                    Text(
                        text = stringResource(R.string.kanji_info_jlpt_template, it.toString()),
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                screenState.frequency?.let {
                    Text(
                        text = stringResource(R.string.kanji_info_frequency_template, it),
                        style = MaterialTheme.typography.titleSmall
                    )
                }

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
            ReadingRow(title = stringResource(R.string.kanji_info_kun), items = screenState.kun)

        if (screenState.on.isNotEmpty())
            ReadingRow(title = stringResource(R.string.kanji_info_on), items = screenState.on)

    }

}

@Composable
private fun AnimatableCharacter(strokes: List<Path>) {

    Column {

        Card(
            modifier = Modifier.size(120.dp),
            elevation = CardDefaults.elevatedCardElevation()
        ) {

            Box(modifier = Modifier.fillMaxSize()) {

                KanjiBackground(Modifier.fillMaxSize())

                AnimatedKanji(
                    strokes = strokes,
                    modifier = Modifier.fillMaxSize()
                )

            }

        }

        Text(
            text = buildAnnotatedString {
                val text = stringResource(R.string.kanji_info_strokes_count, strokes.size)
                append(text)
                val numberStyle = SpanStyle(fontWeight = FontWeight.Bold)
                "\\d+".toRegex().findAll(text).forEach {
                    addStyle(numberStyle, it.range.first, it.range.last + 1)
                }
            },
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally)
        )

    }

}

@Composable
private fun ReadingRow(title: String, items: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.width(8.dp))
        AutoBreakRow(Modifier.weight(1f)) {
            items.forEach { ReadingItem(text = it) }
        }
    }
}

@Composable
private fun ReadingItem(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(top = 4.dp, end = 4.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        maxLines = 1
    )
}

@Preview
@Composable
private fun Preview(
    state: ScreenState = ScreenState.Loading
) {

    AppTheme {
        KanjiInfoScreenUI(
            char = PreviewKanji.kanji,
            state = rememberUpdatedState(state),
            onUpButtonClick = {},
            onCopyButtonClick = {},
            onFuriganaItemClick = {}
        )
    }

}

@Preview
@Composable
private fun NoDataPreview() {
    Preview(ScreenState.NoData)
}

@Preview
@Composable
private fun KanaPreview() {
    Preview(
        state = ScreenState.Loaded.Kana(
            character = "あ",
            strokes = PreviewKanji.strokes,
            radicals = emptyList(),
            words = emptyList(),
            kanaSystem = CharactersClassification.Kana.HIRAGANA,
            reading = "A",
        )
    )
}

@Preview
@Composable
private fun KanjiPreview() {
    Preview(
        state = ScreenState.Loaded.Kanji(
            character = PreviewKanji.kanji,
            strokes = PreviewKanji.strokes,
            radicals = PreviewKanji.radicals,
            meanings = PreviewKanji.meanings,
            on = PreviewKanji.on,
            kun = PreviewKanji.kun,
            grade = 1,
            jlpt = CharactersClassification.JLPT.N5,
            frequency = 1,
            words = PreviewKanji.randomWords(30)
        )
    )
}
