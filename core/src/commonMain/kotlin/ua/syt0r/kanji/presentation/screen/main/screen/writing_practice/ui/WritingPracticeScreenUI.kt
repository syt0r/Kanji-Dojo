package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.trackItemPosition
import ua.syt0r.kanji.presentation.common.ui.CustomRippleTheme
import ua.syt0r.kanji.presentation.common.ui.LocalOrientation
import ua.syt0r.kanji.presentation.common.ui.Material3BottomSheetScaffold
import ua.syt0r.kanji.presentation.common.ui.Orientation
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewUserAction
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.StrokeInputData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.StrokeProcessingResult
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingPracticeCharReviewResult
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingScreenConfiguration
import kotlin.time.DurationUnit

@Composable
fun WritingPracticeScreenUI(
    state: State<ScreenState>,
    navigateBack: () -> Unit,
    onConfigured: (WritingScreenConfiguration) -> Unit,
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
                fadeIn(tween(600)) togetherWith fadeOut(tween(600))
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

                is ScreenState.Configuring -> {
                    ConfiguringState(
                        state = it,
                        onClick = onConfigured
                    )
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
private fun ConfiguringState(
    state: ScreenState.Configuring,
    onClick: (WritingScreenConfiguration) -> Unit
) {

    val strings = resolveString { writingPractice }

    Column(
        modifier = Modifier.fillMaxSize()
            .wrapContentSize()
            .widthIn(max = 400.dp)
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp)
    ) {


        var studyNew by remember {
            mutableStateOf(true)
        }

        var noTranslationLayout by remember {
            mutableStateOf(state.configuration.noTranslationsLayout)
        }

        var leftHandedMode by remember {
            mutableStateOf(state.configuration.leftHandedMode)
        }

        var shuffle by remember {
            mutableStateOf(state.configuration.shuffle)
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
        ) {

            var expanded by remember { mutableStateOf(false) }

            Row(
                Modifier.clip(MaterialTheme.shapes.medium)
                    .clickable(onClick = { expanded = !expanded })
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = strings.collapsablePracticeItemsTitle(state.characters.size),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { expanded = !expanded }) {
                    val icon = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown
                    Icon(imageVector = icon, contentDescription = null)
                }
            }

            if (expanded) {
                Text(
                    text = state.characters.joinToString(""),
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .wrapContentSize(),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            ConfigurationOption(
                title = strings.studyNewTitle,
                subtitle = strings.studyNewMessage,
                enabled = studyNew,
                onChange = { studyNew = it }
            )

            ConfigurationOption(
                title = strings.noTranslationLayoutTitle,
                subtitle = strings.noTranslationLayoutMessage,
                enabled = noTranslationLayout,
                onChange = { noTranslationLayout = it }
            )

            ConfigurationOption(
                title = strings.leftHandedModeTitle,
                subtitle = strings.leftHandedModeMessage,
                enabled = leftHandedMode,
                onChange = { leftHandedMode = it }
            )

            ConfigurationOption(
                title = strings.shuffleTitle,
                subtitle = strings.shuffleMessage,
                enabled = shuffle,
                onChange = { shuffle = it }
            )

        }

        Button(
            onClick = {
                val configuration = WritingScreenConfiguration(
                    studyNew,
                    noTranslationLayout,
                    leftHandedMode,
                    shuffle
                )
                onClick(configuration)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(strings.configurationCompleteButton)
        }

    }

}

@Composable
private fun ConfigurationOption(
    title: String,
    subtitle: String,
    enabled: Boolean,
    onChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = { onChange(!enabled) })
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        }

        Switch(
            checked = enabled,
            onCheckedChange = { onChange(it) },
            colors = SwitchDefaults.colors(
                uncheckedTrackColor = MaterialTheme.colorScheme.background
            )
        )
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

                is ScreenState.Configuring -> {
                    Text(text = resolveString { writingPractice.configurationTitle })
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
            modifier = Modifier.fillMaxSize()
                .wrapContentSize(align = Alignment.TopCenter)
                .widthIn(max = 400.dp),
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
                .wrapContentSize(align = Alignment.TopCenter)
                .widthIn(max = 400.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = contentPaddingState.value),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SavedStateInfoLabel(
                title = resolveString { writingPractice.savedReviewedCountLabel },
                data = screenState.run { goodCharacters.size + repeatCharacters.size }.toString()
            )

            SavedStateInfoLabel(
                title = resolveString { writingPractice.savedTimeSpentLabel },
                data = screenState.practiceDuration.toString(DurationUnit.MINUTES, 2)
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
