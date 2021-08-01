package ua.syt0r.kanji.presentation.screen.screen.writing_dashboard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.user_data.model.PracticeSetInfo
import ua.syt0r.kanji.presentation.common.theme.KanjiDojoTheme
import ua.syt0r.kanji.presentation.common.theme.primary
import ua.syt0r.kanji.presentation.screen.screen.writing_dashboard.WritingDashboardScreenContract
import java.time.LocalDateTime
import kotlin.random.Random

@Composable
fun WritingDashboardScreenUI(
    state: WritingDashboardScreenContract.State,
    onImportPredefinedSet: () -> Unit,
    onCreateCustomSet: () -> Unit,
    onPracticeSetSelected: (PracticeSetInfo) -> Unit
) {

    when (state) {
        WritingDashboardScreenContract.State.Loading -> LoadingState()
        is WritingDashboardScreenContract.State.Loaded -> LoadedState(
            practiceSets = state.practiceSets,
            onPracticeSetCreateOptionSelected = {
                when (it) {
                    DialogOption.PREDEFINED -> onImportPredefinedSet()
                    DialogOption.CUSTOM -> onCreateCustomSet()
                }
            },
            onPracticeSetSelected = { onPracticeSetSelected(it) }
        )
    }

}

@Composable
private fun LoadingState() {
    CircularProgressIndicator(
        strokeWidth = 8.dp,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
            .size(64.dp),
        color = MaterialTheme.colors.primary
    )
}

@Composable
private fun LoadedState(
    practiceSets: List<PracticeSetInfo>,
    onPracticeSetCreateOptionSelected: (DialogOption) -> Unit,
    onPracticeSetSelected: (PracticeSetInfo) -> Unit
) {

    val isDialogOpened = remember { mutableStateOf(false) }

    if (isDialogOpened.value) {
        WritingSetCreationDialog(
            onDismiss = { isDialogOpened.value = false },
            onOptionSelected = {
                isDialogOpened.value = false
                onPracticeSetCreateOptionSelected(it)
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            CreateSetButton { isDialogOpened.value = true }
        }
    ) {

        if (practiceSets.isEmpty()) {
            PracticeSetEmptyState()
        } else {
            PracticeSetList(practiceSets, onPracticeSetSelected)
        }

    }

}

@Composable
private fun CreateSetButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        contentColor = primary
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_add_24),
            contentDescription = ""
        )
    }
}

@Composable
private fun PracticeSetEmptyState() {

    Text(
        text = buildAnnotatedString {

            append("No practice sets created yet. Click on ")

            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colors.secondary,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("+")
            }

            append(" to choose from available or create custom set")

        },
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp)
            .wrapContentSize(),
        textAlign = TextAlign.Center
    )

}

@Composable
private fun PracticeSetList(
    practiceSets: List<PracticeSetInfo>,
    onPracticeSetSelected: (PracticeSetInfo) -> Unit
) {

    LazyColumn {

        items(practiceSets) {
            Column {
                Text(
                    text = it.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPracticeSetSelected(it) }
                        .padding(vertical = 12.dp, horizontal = 24.dp)
                )
            }
        }

    }

}


@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    KanjiDojoTheme {
        WritingDashboardScreenUI(
            state = WritingDashboardScreenContract.State.Loaded(
                practiceSets = emptyList()
            ),
            onImportPredefinedSet = {},
            onCreateCustomSet = {},
            onPracticeSetSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FilledStatePreview() {
    KanjiDojoTheme {
        WritingDashboardScreenUI(
            state = WritingDashboardScreenContract.State.Loaded(
                practiceSets = listOf(
                    PracticeSetInfo(
                        id = Random.nextLong(),
                        name = "Set Name",
                        previewKanji = "A",
                        latestReviewTime = LocalDateTime.now()
                    )
                )
            ),
            onImportPredefinedSet = {},
            onCreateCustomSet = {},
            onPracticeSetSelected = {}
        )
    }
}