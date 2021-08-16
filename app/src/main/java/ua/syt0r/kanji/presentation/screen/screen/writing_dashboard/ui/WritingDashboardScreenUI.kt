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
import androidx.compose.ui.res.stringResource
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
import ua.syt0r.kanji.presentation.common.ui.CustomTopBar
import ua.syt0r.kanji.presentation.screen.screen.writing_dashboard.WritingDashboardScreenContract
import ua.syt0r.kanji.presentation.screen.screen.writing_dashboard.WritingDashboardScreenContract.State.Loaded
import ua.syt0r.kanji.presentation.screen.screen.writing_dashboard.WritingDashboardScreenContract.State.Loading
import java.time.LocalDateTime
import kotlin.random.Random

@Composable
fun WritingDashboardScreenUI(
    state: WritingDashboardScreenContract.State,
    onUpButtonClick: () -> Unit,
    onImportPredefinedSet: () -> Unit,
    onCreateCustomSet: () -> Unit,
    onPracticeSetSelected: (PracticeSetInfo) -> Unit
) {

    val shouldShowDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.writing_dashboard_title),
                upButtonVisible = true,
                onUpButtonClick = onUpButtonClick
            )
        },
        floatingActionButton = {
            if (state is Loaded) {
                CreateSetButton { shouldShowDialog.value = true }
            }
        }
    ) {

        when (state) {
            Loading -> LoadingState()
            is Loaded -> LoadedState(
                practiceSets = state.practiceSets,
                onPracticeSetSelected = onPracticeSetSelected
            )
        }

    }

    if (shouldShowDialog.value) {
        WritingSetCreationDialog(
            onDismiss = { shouldShowDialog.value = false },
            onOptionSelected = {
                shouldShowDialog.value = false
                when (it) {
                    DialogOption.IMPORT -> onImportPredefinedSet()
                    DialogOption.CUSTOM -> onCreateCustomSet()
                }
            }
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
    onPracticeSetSelected: (PracticeSetInfo) -> Unit
) {

    if (practiceSets.isEmpty()) {
        PracticeSetEmptyState()
    } else {
        PracticeSetList(practiceSets, onPracticeSetSelected)
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

            Text(
                text = it.name,
                modifier = Modifier
                    .clickable { onPracticeSetSelected(it) }
                    .fillMaxWidth()
                    .height(60.dp)
                    .wrapContentHeight()
                    .padding(horizontal = 24.dp),
                maxLines = 1
            )

        }

    }

}


@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    KanjiDojoTheme {
        WritingDashboardScreenUI(
            state = Loaded(
                practiceSets = emptyList()
            ),
            onUpButtonClick = {},
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
            state = Loaded(
                practiceSets = listOf(
                    PracticeSetInfo(
                        id = Random.nextLong(),
                        name = "Set Name",
                        previewKanji = "A",
                        latestReviewTime = LocalDateTime.now()
                    )
                )
            ),
            onUpButtonClick = {},
            onImportPredefinedSet = {},
            onCreateCustomSet = {},
            onPracticeSetSelected = {}
        )
    }
}