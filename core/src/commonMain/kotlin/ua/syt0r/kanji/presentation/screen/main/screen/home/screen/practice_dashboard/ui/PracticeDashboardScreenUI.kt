package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.trackScreenHeight
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.PracticeDashboardItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PracticeDashboardScreenUI(
    state: State<ScreenState>,
    onImportPredefinedSet: () -> Unit,
    onCreateCustomSet: () -> Unit,
    onPracticeSetSelected: (PracticeDashboardItem) -> Unit,
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
        val message = resolveString { practiceDashboard.analyticsSuggestionMessage }
        val actionLabel = resolveString { practiceDashboard.analyticsSuggestionAction }
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


    val extraBottomSpacing = remember { mutableStateOf(16.dp) }

    Scaffold(
        floatingActionButton = {
            val shouldShowButton by remember { derivedStateOf { state.value is ScreenState.Loaded } }
            if (shouldShowButton) {
                FloatingActionButton(
                    onClick = { shouldShowCreatePracticeDialog = true },
                    modifier = Modifier.trackScreenHeight {
                        extraBottomSpacing.value = it.heightFromScreenBottom + 16.dp
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
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
                    extraBottomSpacing = extraBottomSpacing,
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
    practiceSets: List<PracticeDashboardItem>,
    extraBottomSpacing: State<Dp>,
    onPracticeSetSelected: (PracticeDashboardItem) -> Unit
) {

    if (practiceSets.isEmpty()) {
        PracticeSetEmptyState()
    } else {
        PracticeSetList(practiceSets, extraBottomSpacing, onPracticeSetSelected)
    }

}

@Composable
private fun PracticeSetEmptyState() {
    val iconColor = MaterialTheme.colorScheme.secondary
    Text(
        text = resolveString { practiceDashboard.emptyMessage(iconColor) },
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp)
            .wrapContentSize(),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun PracticeSetList(
    practiceSets: List<PracticeDashboardItem>,
    extraBottomSpacing: State<Dp>,
    onPracticeSetSelected: (PracticeDashboardItem) -> Unit
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
            Spacer(modifier = Modifier.height(extraBottomSpacing.value))
        }

    }

}

@Composable
private fun PracticeItem(
    practice: PracticeDashboardItem,
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
                text = practice.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = resolveString {
                    practiceDashboard.itemTimeMessage(practice.reviewToNowDuration)
                },
                style = MaterialTheme.typography.bodySmall,
            )

        }


    }

}
