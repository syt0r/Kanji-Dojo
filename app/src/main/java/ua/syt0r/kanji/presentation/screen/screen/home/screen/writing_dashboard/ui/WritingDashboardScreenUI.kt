package ua.syt0r.kanji.presentation.screen.screen.home.screen.writing_dashboard.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import ua.syt0r.kanji.core.user_data.model.Practice
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.screen.home.screen.writing_dashboard.WritingDashboardScreenContract
import ua.syt0r.kanji.presentation.screen.screen.home.screen.writing_dashboard.WritingDashboardScreenContract.State.Loaded
import ua.syt0r.kanji.presentation.screen.screen.home.screen.writing_dashboard.WritingDashboardScreenContract.State.Loading
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingDashboardScreenUI(
    state: WritingDashboardScreenContract.State,
    onUpButtonClick: () -> Unit,
    onImportPredefinedSet: () -> Unit,
    onCreateCustomSet: () -> Unit,
    onPracticeSetSelected: (Practice) -> Unit
) {

    val shouldShowDialog = remember { mutableStateOf(false) }

    Scaffold(
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
                    DialogOption.SELECT -> onImportPredefinedSet()
                    DialogOption.CREATE -> onCreateCustomSet()
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
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun LoadedState(
    practiceSets: List<Practice>,
    onPracticeSetSelected: (Practice) -> Unit
) {

    if (practiceSets.isEmpty()) {
        PracticeSetEmptyState()
    } else {
        PracticeSetList(practiceSets, onPracticeSetSelected)
    }

}

@Composable
private fun CreateSetButton(onClick: () -> Unit) {
    androidx.compose.material3.FloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_add_24),
            contentDescription = null
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
                    color = MaterialTheme.colorScheme.secondary,
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
    practiceSets: List<Practice>,
    onPracticeSetSelected: (Practice) -> Unit
) {

    LazyColumn {

        items(practiceSets) {

            Row(
                modifier = Modifier
                    .clickable { onPracticeSetSelected(it) }
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = it.name,
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    maxLines = 1
                )

            }

        }

        item { Spacer(modifier = Modifier.height(64.dp)) }

    }

}


@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    AppTheme {
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
    AppTheme {
        WritingDashboardScreenUI(
            state = Loaded(
                practiceSets = listOf(
                    Practice(
                        id = Random.nextLong(),
                        name = "Set Name"
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