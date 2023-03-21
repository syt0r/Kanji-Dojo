package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.ui

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.common.PaginatableJapaneseWordList
import ua.syt0r.kanji.presentation.common.isNearListEnd
import ua.syt0r.kanji.presentation.common.jsonSaver
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.trackItemPosition
import ua.syt0r.kanji.presentation.common.ui.ClickableFuriganaText
import ua.syt0r.kanji.presentation.common.ui.LocalOrientation
import ua.syt0r.kanji.presentation.common.ui.Orientation
import ua.syt0r.kanji.presentation.dialog.AlternativeWordsDialog
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract.ScreenState


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun KanjiInfoScreenUI(
    char: String,
    state: State<ScreenState>,
    onUpButtonClick: () -> Unit,
    onCopyButtonClick: () -> Unit,
    onFuriganaItemClick: (String) -> Unit,
    onScrolledToBottom: () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        snapshotFlow {
            listState.isNearListEnd(KanjiInfoScreenContract.StartLoadMoreWordsFromItemsToEnd) &&
                    state.value.let { it as? ScreenState.Loaded }?.words?.value?.canLoadMore == true
        }
            .filter { it }
            .collect { onScrolledToBottom() }
    }

    val fabHeightData = remember { mutableStateOf(16.dp) }

    val shouldShowScrollButton = remember {
        derivedStateOf(policy = structuralEqualityPolicy()) {
            listState.firstVisibleItemIndex != 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = char) },
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
                modifier = Modifier.trackItemPosition {
                    fabHeightData.value = it.heightFromScreenBottom + 16.dp
                }
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
                        text = resolveString { kanjiInfo.noDataMessage },
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }

                is ScreenState.Loaded -> {
                    val clipboardCopyMessage = resolveString { kanjiInfo.clipboardCopyMessage }
                    LoadedState(
                        screenState = screenState,
                        listState = listState,
                        contentBottomPadding = fabHeightData,
                        onCopyButtonClick = {
                            onCopyButtonClick()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    clipboardCopyMessage,
                                    withDismissAction = true
                                )
                            }
                        },
                        onFuriganaItemClick = onFuriganaItemClick,
                        onScrolledToBottom = onScrolledToBottom
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
    contentBottomPadding: State<Dp>,
    onCopyButtonClick: () -> Unit,
    onFuriganaItemClick: (String) -> Unit,
    onScrolledToBottom: () -> Unit
) {

    val selectedWordForAlternativeDialog = rememberSaveable(stateSaver = jsonSaver()) {
        mutableStateOf<JapaneseWord?>(null)
    }
    selectedWordForAlternativeDialog.value?.let {
        AlternativeWordsDialog(
            word = it,
            onDismissRequest = { selectedWordForAlternativeDialog.value = null },
            onFuriganaClick = onFuriganaItemClick
        )
    }

    if (LocalOrientation.current == Orientation.Portrait) {

        LazyColumn(
            state = listState
        ) {

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    KanjiInfoCharacterInfoSection(
                        screenState = screenState,
                        onCopyButtonClick = onCopyButtonClick
                    )
                }
            }

            wordsSection(
                wordsState = screenState.words,
                bottomPaddingState = contentBottomPadding,
                onFuriganaItemClick = onFuriganaItemClick,
                onWordClick = { selectedWordForAlternativeDialog.value = it }
            )

        }
    } else {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                KanjiInfoCharacterInfoSection(
                    screenState = screenState,
                    onCopyButtonClick = onCopyButtonClick
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            ) {
                wordsSection(
                    wordsState = screenState.words,
                    bottomPaddingState = contentBottomPadding,
                    onFuriganaItemClick = onFuriganaItemClick,
                    onWordClick = { selectedWordForAlternativeDialog.value = it }
                )
            }
        }
    }

}

private fun LazyListScope.wordsSection(
    wordsState: State<PaginatableJapaneseWordList>,
    bottomPaddingState: State<Dp>,
    onFuriganaItemClick: (String) -> Unit,
    onWordClick: (JapaneseWord) -> Unit
) {

    val words = wordsState.value

    item {
        Text(
            text = resolveString { kanjiInfo.wordsSectionTitle(words.totalCount) },
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }

    itemsIndexed(words.items) { index, word ->
        ExpressionItem(
            index = index,
            word = word,
            onFuriganaItemClick = onFuriganaItemClick,
            onClick = { onWordClick(word) }
        )
    }

    if (words.canLoadMore) {
        item {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .wrapContentSize()
            )
        }
    }

    item { Spacer(modifier = Modifier.height(bottomPaddingState.value)) }

}

@Composable
private fun ExpressionItem(
    index: Int,
    word: JapaneseWord,
    onFuriganaItemClick: (String) -> Unit,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .heightIn(min = 50.dp)
            .padding(horizontal = 10.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .wrapContentSize(Alignment.CenterStart)
    ) {
        ClickableFuriganaText(
            furiganaString = word.orderedPreview(index),
            onClick = onFuriganaItemClick,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(horizontal = 10.dp)
        )
    }

}

