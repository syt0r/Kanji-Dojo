package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ua.syt0r.kanji.presentation.common.MultiplatformBackHandler
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.*
import ua.syt0r.kanji.presentation.common.trackItemPosition
import ua.syt0r.kanji.presentation.common.ui.*
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.*

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun WritingPracticeScreenUI(
    state: State<ScreenState>,
    navigateBack: () -> Unit,
    submitUserInput: suspend (StrokeInputData) -> StrokeProcessingResult,
    onHintClick: () -> Unit,
    onReviewItemClick: (ReviewResult) -> Unit,
    onPracticeCompleteButtonClick: () -> Unit,
    onNextClick: (ReviewUserAction) -> Unit,
    toggleRadicalsHighlight: () -> Unit
) {

    var shouldShowLeaveConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    if (shouldShowLeaveConfirmationDialog) {
        LeaveConfirmationDialog(
            onDismissRequest = { shouldShowLeaveConfirmationDialog = false },
            onConfirmClick = { navigateBack() }
        )
    }

    Scaffold(
        topBar = {
            Toolbar(
                state = state,
                onUpClick = {
                    if (state.value::class == ScreenState.Summary.Saved::class) navigateBack()
                    else shouldShowLeaveConfirmationDialog = true
                }
            )
        }
    ) { paddingValues ->

        val transition = updateTransition(targetState = state.value, label = "AnimatedContent")
        transition.AnimatedContent(
            transitionSpec = {
                fadeIn(tween(600)) with fadeOut(tween(600))
            },
            contentKey = { it::class },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            when (it) {
                is ScreenState.Review -> {
                    ReviewState(
                        configuration = it.configuration,
                        reviewState = it.reviewState,
                        onStrokeDrawn = submitUserInput,
                        onHintClick = onHintClick,
                        onNextClick = onNextClick,
                        toggleRadicalsHighlight = toggleRadicalsHighlight
                    )
                }
                ScreenState.Loading,
                ScreenState.Summary.Saving -> {
                    LoadingState()
                }
                is ScreenState.Summary.Saved -> {
                    SummaryState(
                        state = rememberUpdatedState(it),
                        onReviewItemClick = onReviewItemClick,
                        onPracticeCompleteButtonClick = onPracticeCompleteButtonClick
                    )
                }
            }

            val shouldHandleBackClicksState = remember {
                derivedStateOf { it !is ScreenState.Summary.Saved }
            }
            if (shouldHandleBackClicksState.value) {
                MultiplatformBackHandler { shouldShowLeaveConfirmationDialog = true }
            }

        }

    }

}

@Composable
private fun LeaveConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {

    MultiplatformDialog(
        onDismissRequest = onDismissRequest
    ) {

        Surface(
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .padding(20.dp)
            ) {

                Text(
                    text = resolveString { writingPractice.leaveDialogTitle },
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                Text(
                    text = resolveString { writingPractice.leaveDialogMessage },
                    style = MaterialTheme.typography.bodyMedium
                )

                TextButton(
                    onClick = onConfirmClick,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .align(Alignment.End)
                ) {
                    Text(text = resolveString { writingPractice.leaveDialogButton })
                }

            }
        }


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    state: State<ScreenState>,
    onUpClick: () -> Unit
) {
    TopAppBar(
        title = {
            when (val screenState = state.value) {
                is ScreenState.Review -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(align = Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val progress = screenState.reviewState.value.progress
                        ToolbarCountItem(
                            count = progress.pendingCount,
                            color = MaterialTheme.extraColorScheme.pending
                        )

                        ToolbarCountItem(
                            count = progress.repeatCount,
                            color = MaterialTheme.colorScheme.primary
                        )

                        ToolbarCountItem(
                            count = progress.finishedCount,
                            color = MaterialTheme.extraColorScheme.success
                        )
                    }
                }
                is ScreenState.Summary -> {
                    Text(text = resolveString { writingPractice.summaryTitle })
                }
                else -> {}
            }
        },
        navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(Icons.Default.ArrowBack, null)
            }
        }
    )
}

@Composable
private fun ToolbarCountItem(count: Int, color: Color) {
    val rippleTheme = remember { CustomRippleTheme(colorProvider = { color }) }
    CompositionLocalProvider(LocalRippleTheme provides rippleTheme) {
        TextButton(onClick = {}) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = count.toString(), color = color)
        }
    }
}

@Composable
private fun LoadingState() {

    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ReviewState(
    configuration: WritingScreenConfiguration,
    reviewState: State<WritingReviewData>,
    onStrokeDrawn: suspend (StrokeInputData) -> StrokeProcessingResult,
    onHintClick: () -> Unit,
    onNextClick: (ReviewUserAction) -> Unit,
    toggleRadicalsHighlight: () -> Unit
) {

    val infoSectionState = reviewState.asInfoSectionState(configuration)
    val inputSectionState = reviewState.asInputSectionState()
    val wordsBottomSheetState = reviewState.asWordsBottomSheetState()

    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val bottomSheetHeightState = remember { mutableStateOf(100.dp) }

    val openBottomSheet: () -> Unit = {
        coroutineScope.launch { scaffoldState.bottomSheetState.expand() }
    }

    if (LocalOrientation.current == Orientation.Portrait) {

        Material3BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                WritingPracticeWordsBottomSheet(
                    state = wordsBottomSheetState,
                    sheetContentHeight = bottomSheetHeightState
                )
            }
        ) {

            val infoSectionBottomPadding = remember { mutableStateOf(0.dp) }

            WritingPracticeInfoSection(
                state = infoSectionState,
                bottomSheetHeight = bottomSheetHeightState,
                onExpressionsClick = openBottomSheet,
                toggleRadicalsHighlight = toggleRadicalsHighlight,
                extraBottomPaddingState = infoSectionBottomPadding,
                modifier = Modifier.fillMaxSize(),
            )

            WritingPracticeInputSection(
                state = inputSectionState,
                onStrokeDrawn = onStrokeDrawn,
                onHintClick = onHintClick,
                onNextClick = onNextClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .trackItemPosition {
                        infoSectionBottomPadding.value = it.heightFromScreenBottom
                    }
                    .sizeIn(maxWidth = 400.dp)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
            )

        }

    } else {

        val infoSection = movableContentWithReceiverOf<RowScope> {
            Material3BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetContent = {
                    WritingPracticeWordsBottomSheet(
                        state = wordsBottomSheetState,
                        sheetContentHeight = bottomSheetHeightState
                    )
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                WritingPracticeInfoSection(
                    state = infoSectionState,
                    bottomSheetHeight = bottomSheetHeightState,
                    onExpressionsClick = openBottomSheet,
                    toggleRadicalsHighlight = toggleRadicalsHighlight,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        val inputSection = movableContentWithReceiverOf<RowScope> {
            WritingPracticeInputSection(
                state = inputSectionState,
                onStrokeDrawn = onStrokeDrawn,
                onHintClick = onHintClick,
                onNextClick = onNextClick,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .wrapContentSize()
                    .sizeIn(maxWidth = 400.dp)
                    .aspectRatio(1f)
                    .padding(20.dp)
            )
        }

        val (firstSection, secondSection) = when (configuration.leftHandedMode) {
            true -> inputSection to infoSection
            false -> infoSection to inputSection
        }

        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            firstSection()

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )

            secondSection()

        }

    }

}

@Composable
private fun SummaryState(
    state: State<ScreenState>,
    onReviewItemClick: (ReviewResult) -> Unit,
    onPracticeCompleteButtonClick: () -> Unit
) {

    val screenState = state.value as ScreenState.Summary.Saved

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        val contentPaddingState = remember { mutableStateOf(16.dp) }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            items(screenState.reviewResultList) {
                SummaryItem(
                    reviewResult = it,
                    onClick = { onReviewItemClick(it) },
                    modifier = Modifier
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(contentPaddingState.value))
            }

        }

        ExtendedFloatingActionButton(
            onClick = onPracticeCompleteButtonClick,
            text = { Text(text = resolveString { writingPractice.summaryButton }) },
            icon = { Icon(Icons.Default.Check, null) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp)
                .trackItemPosition { contentPaddingState.value = it.heightFromScreenBottom + 16.dp }
        )

    }

}

@Composable
private fun SummaryItem(
    reviewResult: ReviewResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val (bgColor, textColor) = when (reviewResult.reviewScore) {
            ReviewScore.Good -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
            ReviewScore.Bad -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        }

        Text(
            text = reviewResult.characterReviewResult.character,
            fontSize = 35.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier
                .size(60.dp)
                .background(bgColor, CircleShape)
                .clip(CircleShape)
                .clickable(onClick = onClick)
                .wrapContentSize()
                .offset(x = (-1).dp, y = (-1).dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = resolveString {
                writingPractice.summaryMistakesMessage(reviewResult.characterReviewResult.mistakes)
            },
            color = when (reviewResult.reviewScore) {
                ReviewScore.Good -> MaterialTheme.colorScheme.onSurface
                ReviewScore.Bad -> MaterialTheme.colorScheme.primary
            },
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

    }

}
