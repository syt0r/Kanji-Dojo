package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.user_data.model.CharacterReviewOutcome
import ua.syt0r.kanji.core.user_data.model.OutcomeSelectionConfiguration
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
    toggleRadicalsHighlight: () -> Unit,
    submitUserInput: suspend (StrokeInputData) -> StrokeProcessingResult,
    onHintClick: () -> Unit,
    onNextClick: (ReviewUserAction) -> Unit,
    onPracticeSaveClick: (OutcomeSelectionConfiguration, Map<String, CharacterReviewOutcome>) -> Unit,
    onPracticeCompleteButtonClick: () -> Unit
) {

    var shouldShowLeaveConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    if (shouldShowLeaveConfirmationDialog) {
        LeaveConfirmationDialog(
            onDismissRequest = { shouldShowLeaveConfirmationDialog = false },
            onConfirmClick = { navigateBack() }
        )
    }

    val shouldShowLeaveConfirmationOnBackClick = remember {
        derivedStateOf { state.value !is ScreenState.Saved }
    }

    if (shouldShowLeaveConfirmationOnBackClick.value) {
        MultiplatformBackHandler { shouldShowLeaveConfirmationDialog = true }
    }

    Scaffold(
        topBar = {
            Toolbar(
                state = state,
                onUpClick = {
                    if (shouldShowLeaveConfirmationOnBackClick.value) {
                        shouldShowLeaveConfirmationDialog = true
                    } else {
                        navigateBack()
                    }
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
                ScreenState.Loading -> {
                    LoadingState()
                }

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

                is ScreenState.Saving -> {
                    SavingState(
                        screenState = it,
                        onSaveClick = onPracticeSaveClick
                    )
                }

                is ScreenState.Saved -> {
                    SavedState(
                        screenState = it,
                        onFinishClick = onPracticeCompleteButtonClick
                    )
                }
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
                ScreenState.Loading -> {}
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

                is ScreenState.Saving -> {
                    Text(text = resolveString { writingPractice.savingTitle })
                }

                is ScreenState.Saved -> {
                    Text(text = resolveString { writingPractice.savedTitle })
                }
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
private fun SavingState(
    screenState: ScreenState.Saving,
    onSaveClick: (OutcomeSelectionConfiguration, Map<String, CharacterReviewOutcome>) -> Unit,
) {

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        val contentPaddingState = remember { mutableStateOf(16.dp) }

        val toleratedMistakesCount = remember {
            mutableStateOf(screenState.outcomeSelectionConfiguration.toleratedMistakesCount)
        }

        val outcomes = remember(toleratedMistakesCount.value) {
            val limit = toleratedMistakesCount.value
            val outcomes = screenState.reviewResultList.map {
                it.character to if (it.mistakes > limit) CharacterReviewOutcome.Fail else CharacterReviewOutcome.Success
            }
            outcomes.toMutableStateMap()
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(80.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {

                Column {

                    var editSectionExpanded by remember { mutableStateOf(false) }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = resolveString { writingPractice.savingPreselectTitle },
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { editSectionExpanded = !editSectionExpanded }) {
                            Icon(Icons.Outlined.Settings, null)
                        }
                    }

                    AnimatedVisibility(
                        visible = editSectionExpanded
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("0")
                                Slider(
                                    value = toleratedMistakesCount.value.toFloat(),
                                    onValueChange = { toleratedMistakesCount.value = it.toInt() },
                                    valueRange = 0f..10f,
                                    steps = 11,
                                    modifier = Modifier.padding(horizontal = 8.dp).weight(1f)
                                )
                                Text("10")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null)
                                Text(
                                    text = resolveString {
                                        writingPractice.savingPreselectCount(toleratedMistakesCount.value)
                                    },
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }

                }

            }

            items(screenState.reviewResultList) {
                val isSelected = outcomes[it.character] == CharacterReviewOutcome.Fail
                SavingStateItem(
                    item = it,
                    isSelected = isSelected,
                    onClick = {
                        outcomes[it.character] =
                            if (isSelected) CharacterReviewOutcome.Success
                            else CharacterReviewOutcome.Fail
                    },
                    modifier = Modifier
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(contentPaddingState.value))
            }

        }

        ExtendedFloatingActionButton(
            onClick = {
                onSaveClick(
                    OutcomeSelectionConfiguration(toleratedMistakesCount.value),
                    outcomes
                )
            },
            text = { Text(text = resolveString { writingPractice.savingButton }) },
            icon = { Icon(Icons.Default.Save, null) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp)
                .trackItemPosition { contentPaddingState.value = it.heightFromScreenBottom + 16.dp }
        )

    }

}

@Composable
private fun SavingStateItem(
    item: WritingPracticeCharReviewResult,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val textColor = when (isSelected) {
            false -> MaterialTheme.colorScheme.onSurface
            true -> MaterialTheme.colorScheme.primary
        }

        Text(
            text = item.character,
            fontSize = 35.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier
        )

        Text(
            text = resolveString { writingPractice.savingMistakesMessage(item.mistakes) },
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        )

    }

}

@Composable
private fun SavedState(
    screenState: ScreenState.Saved,
    onFinishClick: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        val contentPaddingState = remember { mutableStateOf(16.dp) }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            SavedStateInfoLabel(
                title = resolveString { writingPractice.savedReviewedCountLabel },
                data = screenState.run { goodCharacters.size + repeatCharacters.size }.toString()
            )

            SavedStateInfoLabel(
                title = resolveString { writingPractice.savedTimeSpentLabel },
                data = screenState.practiceDuration.toString()
            )

            SavedStateInfoLabel(
                title = resolveString { writingPractice.savedAccuracyLabel },
                data = "%.2f%%".format(screenState.accuracy)
            )

            SavedStateInfoLabel(
                title = resolveString { writingPractice.savedRepeatCharactersLabel },
                data = screenState.repeatCharacters.size.toString()
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(screenState.repeatCharacters) { SavedStateCharacter(it) }
            }

            SavedStateInfoLabel(
                title = resolveString { writingPractice.savedRetainedCharactersLabel },
                data = screenState.goodCharacters.size.toString()
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(screenState.goodCharacters) { SavedStateCharacter(it) }
            }

        }

        ExtendedFloatingActionButton(
            onClick = onFinishClick,
            text = { Text(text = resolveString { writingPractice.savedButton }) },
            icon = { Icon(Icons.Default.Check, null) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp)
                .trackItemPosition { contentPaddingState.value = it.heightFromScreenBottom + 16.dp }
        )

    }

}

@Composable
private fun SavedStateInfoLabel(title: String, data: String) {
    Text(
        buildAnnotatedString {
            withStyle(
                SpanStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                append(title)
            }

            append(" ")

            withStyle(SpanStyle(fontSize = 30.sp, fontWeight = FontWeight.Light)) {
                append(data)
            }
        }
    )
}

@Composable
private fun SavedStateCharacter(character: String) {
    Text(
        text = character,
        fontSize = 32.sp,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .height(IntrinsicSize.Min)
            .aspectRatio(1f, true)
            .padding(8.dp)
            .wrapContentSize()
    )
}
