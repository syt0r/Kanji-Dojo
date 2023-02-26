package ua.syt0r.kanji.presentation.screen.main.screen.practice_import.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.presentation.common.detectUrlClick
import ua.syt0r.kanji.presentation.common.stringResourceWithHtmlUrls
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.furiganaStringResource
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.PracticeImportScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data.ImportPracticeCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeImportScreenUI(
    state: State<ScreenState>,
    onUpButtonClick: () -> Unit = {},
    onItemSelected: (classification: CharactersClassification, title: String) -> Unit = { _, _ -> },
    onLinkClick: (String) -> Unit = {}
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.practice_import_title)) },
                navigationIcon = {
                    IconButton(onClick = onUpButtonClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {

        Crossfade(
            targetState = state.value,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) { screenState ->

            when (screenState) {
                ScreenState.Loading -> LoadingState()
                is ScreenState.Loaded -> LoadedState(
                    screenState = screenState,
                    onItemClick = onItemSelected,
                    onLinkClick = onLinkClick
                )
            }

        }

    }

}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LoadedState(
    screenState: ScreenState.Loaded,
    onItemClick: (classification: CharactersClassification, title: String) -> Unit = { _, _ -> },
    onLinkClick: (String) -> Unit
) {

    var expandedStates by rememberSaveable { mutableStateOf(mapOf<Int, Boolean>()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        screenState.categories.forEachIndexed { index, category ->

            val isExpanded = expandedStates[category.title] == true

            item {

                val onHeaderClick = {
                    expandedStates = expandedStates.plus(category.title to !isExpanded)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onHeaderClick)
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FuriganaText(
                        furiganaString = furiganaStringResource(category.title),
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.titleLarge
                    )

                    IconButton(
                        onClick = onHeaderClick,
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        val icon = if (isExpanded) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        }
                        Icon(icon, null)
                    }

                }

            }

            if (isExpanded) {

                item {
                    val description = stringResourceWithHtmlUrls(category.description)
                    ClickableText(
                        text = description,
                        onClick = { position -> description.detectUrlClick(position, onLinkClick) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                items(category.items) {
                    val title = it.title()
                    ClickableCharacterRow(
                        char = it.previewCharacter,
                        text = title,
                        onClick = { onItemClick(it.classification, title) }
                    )
                }

            }

            val isLast = index == screenState.categories.size - 1
            if (!isLast) {
                item {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                }
            }

        }

    }
}

@Composable
private fun ClickableCharacterRow(
    char: Char,
    text: String,
    onClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Card(
            modifier = Modifier.size(46.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = char.toString(), fontSize = 30.sp)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            modifier = Modifier.fillMaxWidth()
        )

    }

}


@Preview
@Composable
private fun LoadingPreview() {
    AppTheme {
        PracticeImportScreenUI(state = rememberUpdatedState(ScreenState.Loading))
    }
}

@Preview
@Composable
private fun LoadedPreview() {
    AppTheme {
        PracticeImportScreenUI(
            state = rememberUpdatedState(ScreenState.Loaded(ImportPracticeCategory.all))
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ClickableCharacterRowPreview() {

    AppTheme {
        ClickableCharacterRow(char = 'あ', text = "Hiragana")
    }

}
