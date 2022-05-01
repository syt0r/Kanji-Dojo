package ua.syt0r.kanji.presentation.screen.screen.practice_import.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.screen.practice_import.PracticeImportScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.practice_import.data.ImportPracticeCategory
import ua.syt0r.kanji.presentation.screen.screen.practice_import.data.ImportPracticeItem
import ua.syt0r.kanji.presentation.screen.screen.practice_import.data.kanaImportPracticeCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingPracticeImportScreenUI(
    screenState: ScreenState,
    onUpButtonClick: () -> Unit = {},
    onItemSelected: (ImportPracticeItem) -> Unit = {}
) {

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = stringResource(R.string.writing_practice_import_title)) },
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
            targetState = screenState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) { screenState ->

            when (screenState) {
                ScreenState.Loading -> LoadingState()
                is ScreenState.Loaded -> LoadedState(
                    screenState = screenState,
                    onItemClick = onItemSelected
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

private val horizontalPadding = 20.dp
private val verticalPadding = 16.dp

@Composable
private fun LoadedState(
    screenState: ScreenState.Loaded,
    onItemClick: (ImportPracticeItem) -> Unit
) {
    val listData = remember {
        screenState.categories.mapIndexed { index, category ->
            index to category
        }
    }
    LazyColumn(Modifier.fillMaxSize()) {

        items(listData) {

            ExpandableCategorySection(
                category = it.second,
                onItemClick = onItemClick
            )

            val isLast = it.first == screenState.categories.size - 1
            if (!isLast) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding)
                )
            }

        }

    }
}

@Composable
private fun ExpandableSection(
    titleContent: @Composable () -> Unit,
    expandableContent: @Composable () -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {

        titleContent()

    }

    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        expandableContent()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
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
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandableCategorySection(
    category: ImportPracticeCategory,
    onItemClick: (ImportPracticeItem) -> Unit
) {

    ExpandableSection(
        titleContent = {
            Text(
                text = category.title,
                modifier = Modifier.padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding
                ),
                style = MaterialTheme.typography.titleLarge
            )
        },
        expandableContent = {

            Column {

                Text(
                    text = category.description,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding),
                    style = MaterialTheme.typography.titleSmall
                )

                category.items.forEach {
                    ClickableCharacterRow(
                        char = it.char,
                        text = it.title,
                        onClick = { onItemClick(it) }
                    )
                }

            }

        }
    )

}


@Preview
@Composable
private fun LoadingPreview() {
    AppTheme {
        WritingPracticeImportScreenUI(
            screenState = ScreenState.Loading
        )
    }
}

@Preview
@Composable
private fun LoadedPreview() {
    AppTheme {
        WritingPracticeImportScreenUI(
            screenState = ScreenState.Loaded(listOf(kanaImportPracticeCategory))
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
