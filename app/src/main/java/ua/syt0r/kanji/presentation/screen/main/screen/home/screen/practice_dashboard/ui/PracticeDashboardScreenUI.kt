package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.ui

import android.text.format.DateUtils
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.user_data.model.ReviewedPractice
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract.ScreenState
import java.time.Instant
import java.time.OffsetDateTime
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PracticeDashboardScreenUI(
    state: State<ScreenState>,
    onImportPredefinedSet: () -> Unit = {},
    onCreateCustomSet: () -> Unit = {},
    onPracticeSetSelected: (ReviewedPractice) -> Unit = {},
    onAnalyticsSuggestionAccepted: () -> Unit = {},
    onAnalyticsSuggestionDismissed: () -> Unit = {}
) {

    var shouldShowDialog by remember { mutableStateOf(false) }
    if (shouldShowDialog) {
        CreatePracticeOptionDialog(
            onDismiss = { shouldShowDialog = false },
            onOptionSelected = {
                shouldShowDialog = false
                when (it) {
                    DialogOption.SELECT -> onImportPredefinedSet()
                    DialogOption.CREATE -> onCreateCustomSet()
                }
            }
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val shouldShowAnalyticsMessage by remember {
        derivedStateOf {
            state.value.let { it as? ScreenState.Loaded }?.shouldShowAnalyticsSuggestion == true
        }
    }

    if (shouldShowAnalyticsMessage) {
        val message = stringResource(R.string.practice_dashboard_analytics_suggestion_message)
        val actionLabel = stringResource(R.string.practice_dashboard_analytics_suggestion_action)
        LaunchedEffect(Unit) {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            )
            when (result) {
                SnackbarResult.ActionPerformed -> onAnalyticsSuggestionAccepted()
                SnackbarResult.Dismissed -> onAnalyticsSuggestionDismissed()
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            val shouldShowButton by remember { derivedStateOf { state.value is ScreenState.Loaded } }
            if (shouldShowButton) {
                CreateSetButton { shouldShowDialog = true }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        actionColor = MaterialTheme.colorScheme.secondary
                    )
                }
            )
        }
    ) { paddingValues ->

        val transition = updateTransition(targetState = state.value, label = "Content Transition")
        transition.Crossfade(
            contentKey = { it::class },
            modifier = Modifier.padding(paddingValues)
        ) { screenState ->
            when (screenState) {
                ScreenState.Loading -> LoadingState()
                is ScreenState.Loaded -> LoadedState(
                    practiceSets = screenState.practiceSets,
                    onPracticeSetSelected = onPracticeSetSelected
                )
            }
        }

    }

}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}

@Composable
private fun LoadedState(
    practiceSets: List<ReviewedPractice>,
    onPracticeSetSelected: (ReviewedPractice) -> Unit
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

            val message = stringResource(R.string.practice_dashboard_empty_list_message)
            append(message)

            val plusPosition = message.indexOf('+')
            addStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                ),
                start = plusPosition,
                end = plusPosition + 1
            )

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
    practiceSets: List<ReviewedPractice>,
    onPracticeSetSelected: (ReviewedPractice) -> Unit
) {

    LazyColumn {

        items(practiceSets) {

            PracticeItem(
                practice = it,
                onItemClick = { onPracticeSetSelected(it) }
            )

        }

        item { Spacer(modifier = Modifier.height(64.dp)) }

    }

}

@Composable
private fun PracticeItem(
    practice: ReviewedPractice,
    onItemClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .clickable(onClick = onItemClick)
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(Modifier.fillMaxWidth()) {

            Text(
                text = practice.name,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium
            )


            Text(
                text = practice.timestamp
                    ?.let {
                        val zoneOffset = OffsetDateTime.now().offset
                        val lastReviewMillis = it.toInstant(zoneOffset).toEpochMilli()
                        val currentMillis = Instant.now().toEpochMilli()
                        DateUtils.getRelativeTimeSpanString(
                            lastReviewMillis,
                            currentMillis,
                            DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_RELATIVE
                        ).toString()
                    }
                    ?: stringResource(R.string.practice_dashboard_item_not_reviewed),
                style = MaterialTheme.typography.bodySmall,
            )

        }


    }

}


@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    AppTheme {
        PracticeDashboardScreenUI(
            state = ScreenState.Loaded(
                practiceSets = emptyList(),
                shouldShowAnalyticsSuggestion = false
            ).run { rememberUpdatedState(newValue = this) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FilledStatePreview() {
    AppTheme {
        PracticeDashboardScreenUI(
            state = ScreenState.Loaded(
                practiceSets = listOf(
                    ReviewedPractice(
                        id = Random.nextLong(),
                        name = "Set Name",
                        null
                    )
                ),
                shouldShowAnalyticsSuggestion = false
            ).run { rememberUpdatedState(newValue = this) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PracticeItemPreview() {
    AppTheme {
        PracticeItem(
            practice = ReviewedPractice(0, "JLPT N5", null)
        )
    }
}