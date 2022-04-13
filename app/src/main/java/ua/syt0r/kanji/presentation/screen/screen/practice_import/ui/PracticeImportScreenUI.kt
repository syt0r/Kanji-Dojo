package ua.syt0r.kanji.presentation.screen.screen.practice_import.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.screen.practice_import.PracticeImportScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.practice_import.data.PracticeImportItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingPracticeImportScreenUI(
    state: State,
    onUpButtonClick: () -> Unit = {}
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

        when (state) {
            State.Loading -> LoadingState()
            is State.Loaded -> LoadedState(
                state = state,
                onItemClick = {}
            )
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
    state: State.Loaded,
    onItemClick: (PracticeImportItem) -> Unit
) {
    LazyColumn {

        items(state.items) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(it) }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {

                Text(
                    text = it.displayName,
                    modifier = Modifier.fillMaxWidth()
                )

            }

        }

    }
}


@Preview
@Composable
private fun LoadingPreview() {
    AppTheme {
        WritingPracticeImportScreenUI(
            state = State.Loading
        )
    }
}

@Preview
@Composable
private fun LoadedPreview() {
    AppTheme {
        WritingPracticeImportScreenUI(
            state = State.Loaded(
                items = (5 downTo 1).map {
                    PracticeImportItem("JLPT N$it", "")
                }
            )
        )
    }
}
