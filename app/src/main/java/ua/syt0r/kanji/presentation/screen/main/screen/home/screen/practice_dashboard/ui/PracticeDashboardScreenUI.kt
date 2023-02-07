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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.user_data.model.ReviewedPractice
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract.ScreenState
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PracticeDashboardScreenUI(
    state: State<ScreenState>,
    onImportPredefinedSet: () -> Unit,
    onCreateCustomSet: () -> Unit,
    onPracticeSetSelected: (ReviewedPractice) -> Unit,
    onAnalyticsSuggestionAccepted: () -> Unit,
    onAnalyticsSuggestionDismissed: () -> Unit
) {

    var shouldShowCreatePracticeDialog by remember { mutableStateOf(false) }
    if (shouldShowCreatePracticeDialog) {
        CreatePracticeOptionDialog(
            onDismiss = { shouldShowCreatePracticeDialog = false },
            onOptionSelected = {
                shouldShowCreatePracticeDialog = false
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


    val fabLayoutCoordinates = remember { mutableStateOf<LayoutCoordinates?>(null) }

    Scaffold(
        floatingActionButton = {
            val shouldShowButton by remember { derivedStateOf { state.value is ScreenState.Loaded } }
            if (shouldShowButton) {
                FloatingActionButton(
                    onClick = { shouldShowCreatePracticeDialog = true },
                    modifier = Modifier.onPlaced { fabLayoutCoordinates.value = it }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_add_24),
                        contentDescription = null
                    )
                }
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
                    fabLayoutCoordinates = fabLayoutCoordinates,
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
    fabLayoutCoordinates: State<LayoutCoordinates?>,
    onPracticeSetSelected: (ReviewedPractice) -> Unit
) {

    if (practiceSets.isEmpty()) {
        PracticeSetEmptyState()
    } else {
        PracticeSetList(practiceSets, fabLayoutCoordinates, onPracticeSetSelected)
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
    fabLayoutCoordinates: State<LayoutCoordinates?>,
    onPracticeSetSelected: (ReviewedPractice) -> Unit
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
            .widthIn(max = 400.dp)
            .padding(horizontal = 10.dp)
    ) {

        item { Spacer(modifier = Modifier.height(4.dp)) }

        items(practiceSets) {

            PracticeItem(
                practice = it,
                onItemClick = { onPracticeSetSelected(it) }
            )

        }

        item {
            val density = LocalDensity.current.density
            val extraBottomSpacing by remember {
                derivedStateOf {
                    fabLayoutCoordinates.value
                        ?.let {
                            val screenHeight = it.findRootCoordinates().size.height
                            val fabTopHeight = it.boundsInRoot().top
                            (screenHeight - fabTopHeight) / density + 16
                        }
                        ?.dp
                        ?: 16.dp
                }
            }
            Spacer(modifier = Modifier.height(extraBottomSpacing))
        }

    }

}

@Composable
private fun PracticeItem(
    practice: ReviewedPractice,
    onItemClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onItemClick)
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(Modifier.fillMaxWidth()) {

            Text(
                text = practice.name,
                style = MaterialTheme.typography.titleMedium
            )


            Text(
                text = practice.timestamp
                    ?.let {
                        val zoneOffset = OffsetDateTime.now().offset
                        val lastReviewMillis = it.toInstant(zoneOffset).toEpochMilli()
                        val currentMillis = Instant.now().toEpochMilli()
                        val relativeDateText = DateUtils.getRelativeTimeSpanString(
                            lastReviewMillis,
                            currentMillis,
                            DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_RELATIVE
                        ).toString()
                        stringResource(
                            R.string.practice_dashboard_item_review_time,
                            relativeDateText
                        )
                    }
                    ?: stringResource(R.string.practice_dashboard_item_not_reviewed),
                style = MaterialTheme.typography.bodySmall,
            )

        }


    }

}


@Preview
@Composable
private fun Preview(
    state: ScreenState = ScreenState.Loaded(
        practiceSets = emptyList(),
        shouldShowAnalyticsSuggestion = false
    ),
    useDarkTheme: Boolean = false,
) {
    AppTheme(useDarkTheme) {
        PracticeDashboardScreenUI(
            state = rememberUpdatedState(newValue = state),
            onImportPredefinedSet = {},
            onCreateCustomSet = {},
            onPracticeSetSelected = {},
            onAnalyticsSuggestionAccepted = {},
            onAnalyticsSuggestionDismissed = {}
        )
    }
}

@Preview
@Composable
fun PracticeDashboardUIPreview() {
    Preview(
        state = ScreenState.Loaded(
            practiceSets = (0..20).map {
                ReviewedPractice(
                    id = Random.nextLong(),
                    name = "Grade $it",
                    timestamp = if (it % 2 == 0) null
                    else LocalDateTime.now().minusDays(it.toLong())
                )
            },
            shouldShowAnalyticsSuggestion = false
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun PracticeItemPreview() {
    AppTheme {
        PracticeItem(
            practice = ReviewedPractice(0, "JLPT N5", null),
            onItemClick = {}
        )
    }
}

@Preview(device = Devices.PIXEL_C)
@Composable
private fun TabletPreview() {
    Preview(
        state = ScreenState.Loaded(
            practiceSets = (0..10).map {
                ReviewedPractice(
                    id = Random.nextLong(),
                    name = "Grade $it",
                    timestamp = if (it % 2 == 0) null else LocalDateTime.now()
                        .minusDays(it.toLong())
                )
            },
            shouldShowAnalyticsSuggestion = false
        ),
        useDarkTheme = true
    )
}