package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.ui

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.ClickableFuriganaText
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreenContract.ScreenState
import ua.syt0r.kanji.presentation.dialog.AlternativeWordsDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenUI(
    state: State<ScreenState>,
    onSubmitInput: (String) -> Unit,
    onCharacterClick: (String) -> Unit
) {

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // TODO move to topbar with scroll behaviour when stable
            InputSection(onSubmitInput)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            ) {
                val isProgressVisible = remember { derivedStateOf { state.value.isLoading } }
                androidx.compose.animation.AnimatedVisibility(
                    visible = isProgressVisible.value,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxSize(),
                        trackColor = MaterialTheme.colorScheme.background
                    )
                }
            }

            var selectedWord by remember { mutableStateOf<JapaneseWord?>(null) }
            selectedWord?.also {
                AlternativeWordsDialog(
                    word = it,
                    onDismissRequest = { selectedWord = null },
                    onFuriganaClick = onCharacterClick
                )
            }

            ListContent(
                screenState = state.value,
                onCharacterClick = onCharacterClick,
                onWordClick = { selectedWord = it }
            )

        }

    }

}

@Composable
private fun InputSection(
    onSubmitInput: (String) -> Unit
) {

    val enteredText = rememberSaveable { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }
    val isInputFocused = remember { mutableStateOf(false) }

    val isHintVisible = remember {
        derivedStateOf { !isInputFocused.value && enteredText.value.isEmpty() }
    }
    val color = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 20.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(start = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = enteredText.value,
                onValueChange = {
                    enteredText.value = it
                    onSubmitInput(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isInputFocused.value = it.isFocused },
                maxLines = 1,
                singleLine = true,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(color),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color)
            )

            androidx.compose.animation.AnimatedVisibility(
                visible = isHintVisible.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = stringResource(R.string.search_input_hint),
                    style = MaterialTheme.typography.titleMedium,
                    color = color.copy(alpha = 0.7f)
                )
            }
        }
        IconButton(
            onClick = {
                enteredText.value = ""
                onSubmitInput("")
            }
        ) {
            Icon(Icons.Default.Close, null)
        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListContent(
    screenState: ScreenState,
    onCharacterClick: (String) -> Unit,
    onWordClick: (JapaneseWord) -> Unit
) {

    val indexedWords = remember(screenState) {
        screenState.words.mapIndexed { index, word -> index to word }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        item {
            Text(
                text = stringResource(
                    R.string.search_characters_title,
                    screenState.characters.size
                ),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .wrapContentSize(Alignment.CenterStart)
            )
        }

        item {

            LazyRow(modifier = Modifier.fillMaxWidth()) {
                item { Spacer(modifier = Modifier.width(20.dp)) }
                items(screenState.characters) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .padding(vertical = 16.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onCharacterClick(it) }
                            .padding(8.dp)
                            .height(IntrinsicSize.Min)
                            .aspectRatio(1f, true)
                            .wrapContentSize()
                    )
                }
            }

        }

        stickyHeader {
            Text(
                text = stringResource(R.string.search_words_title, screenState.words.size),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .wrapContentSize(Alignment.CenterStart)
            )
        }

        items(indexedWords) { (index, word) ->
            ClickableFuriganaText(
                furiganaString = word.orderedPreview(index),
                onClick = { onCharacterClick(it) },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onWordClick(word) }
                    .padding(vertical = 8.dp, horizontal = 12.dp)
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

    }

}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    AppTheme {
        SearchScreenUI(
            state = rememberUpdatedState(ScreenState(isLoading = true)),
            onSubmitInput = {},
            onCharacterClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadedStatePreview() {
    AppTheme(useDarkTheme = false) {
        SearchScreenUI(
            state = ScreenState(
                isLoading = false,
                characters = (0 until 10).map { PreviewKanji.randomKanji() },
                words = PreviewKanji.randomWords(20)
            ).run { rememberUpdatedState(newValue = this) },
            onSubmitInput = {},
            onCharacterClick = {}
        )
    }
}