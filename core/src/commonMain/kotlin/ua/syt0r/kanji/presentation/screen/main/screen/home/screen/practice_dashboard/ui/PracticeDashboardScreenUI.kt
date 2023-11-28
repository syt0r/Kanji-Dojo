package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import ua.syt0r.kanji.core.app_state.DailyGoalConfiguration
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.customBlue
import ua.syt0r.kanji.presentation.common.theme.customOrange
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.trackItemPosition
import ua.syt0r.kanji.presentation.common.ui.CustomRippleTheme
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.PracticeDashboardScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.DailyIndicatorData
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.DailyProgress
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.data.PracticeDashboardItem

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PracticeDashboardScreenUI(
    state: State<ScreenState>,
    onImportPredefinedSet: () -> Unit,
    onCreateCustomSet: () -> Unit,
    onPracticeSetSelected: (PracticeDashboardItem) -> Unit,
    quickPractice: (MainDestination.Practice) -> Unit,
    updateDailyGoalConfiguration: (DailyGoalConfiguration) -> Unit
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

    val extraBottomSpacing = remember { mutableStateOf(16.dp) }

    Scaffold(
        floatingActionButton = {
            val shouldShowButton by remember { derivedStateOf { state.value is ScreenState.Loaded } }
            AnimatedVisibility(
                visible = shouldShowButton,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = { shouldShowCreatePracticeDialog = true },
                    modifier = Modifier.trackItemPosition {
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
        bottomBar = {
            DailyIndicator(
                state = derivedStateOf {
                    state.value.let { it as? ScreenState.Loaded }?.dailyIndicatorData
                },
                updateConfiguration = updateDailyGoalConfiguration
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
                    screenState = screenState,
                    extraBottomSpacing = extraBottomSpacing,
                    onPracticeSetSelected = onPracticeSetSelected,
                    quickPractice = quickPractice
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
    screenState: ScreenState.Loaded,
    extraBottomSpacing: State<Dp>,
    onPracticeSetSelected: (PracticeDashboardItem) -> Unit,
    quickPractice: (MainDestination.Practice) -> Unit
) {

    if (screenState.practiceSets.isEmpty()) {
        PracticeSetEmptyState()
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
            .widthIn(max = 400.dp)
            .padding(horizontal = 10.dp)
    ) {

        item { Spacer(modifier = Modifier.height(4.dp)) }

        items(
            items = screenState.practiceSets,
            key = { it.practiceId }
        ) {

            ListItem(
                practice = it,
                dailyGoalEnabled = screenState.dailyIndicatorData.configuration.enabled,
                onItemClick = { onPracticeSetSelected(it) },
                quickPractice = quickPractice
            )

        }

        item {
            Spacer(modifier = Modifier.height(extraBottomSpacing.value))
        }

    }

}

@Composable
private fun PracticeSetEmptyState() {
    val iconColor = MaterialTheme.colorScheme.secondary
    Text(
        text = resolveString { practiceDashboard.emptyScreenMessage(iconColor) },
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp)
            .wrapContentSize(),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ListItem(
    practice: PracticeDashboardItem,
    dailyGoalEnabled: Boolean,
    onItemClick: () -> Unit,
    quickPractice: (MainDestination.Practice) -> Unit
) {

    var expanded by rememberSaveable(practice.practiceId) { mutableStateOf(false) }

    Column(
        modifier = Modifier.clip(MaterialTheme.shapes.large)
    ) {

        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .clickable(onClick = { expanded = !expanded })
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Column(
                modifier = Modifier.weight(1f)
                    .padding(start = 10.dp)
                    .padding(vertical = 10.dp),
            ) {

                Text(
                    text = practice.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = resolveString {
                        practiceDashboard.itemTimeMessage(practice.timeSinceLastPractice)
                    },
                    style = MaterialTheme.typography.bodySmall,
                )

            }

            if (dailyGoalEnabled) {
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    PracticeSneakPeekIndicator(
                        icon = Icons.Default.Draw,
                        study = practice.writingProgress.quickLearn.size,
                        review = practice.writingProgress.quickReview.size
                    )
                    PracticeSneakPeekIndicator(
                        icon = Icons.Default.LocalLibrary,
                        study = practice.readingProgress.quickLearn.size,
                        review = practice.readingProgress.quickReview.size
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable(onClick = onItemClick)
                    .padding(horizontal = 20.dp)
                    .wrapContentSize()
            ) {
                Icon(Icons.Default.KeyboardArrowRight, null)
            }

        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            ListItemDetails(practice, quickPractice)
        }

    }

}

@Composable
private fun PracticeSneakPeekIndicator(icon: ImageVector, study: Int, review: Int) {
    if (study == 0 && review == 0) return
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
        if (study > 0) {
            Box(
                modifier = Modifier.align(Alignment.CenterVertically).size(4.dp)
                    .background(customBlue, CircleShape)
            )
        }
        if (review > 0) {
            Box(
                modifier = Modifier.align(Alignment.CenterVertically).size(4.dp)
                    .background(customOrange, CircleShape)
            )
        }
    }
}

@Composable
private fun ListItemDetails(
    data: PracticeDashboardItem,
    quickPractice: (MainDestination.Practice) -> Unit
) {

    val strings = resolveString { practiceDashboard }

    var isReadingMode by rememberSaveable(data.practiceId) { mutableStateOf(false) }
    val studyProgress = remember(data to isReadingMode) {
        if (isReadingMode) data.readingProgress else data.writingProgress
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {

        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.align(Alignment.Start)
                ) {


                    Switch(
                        checked = isReadingMode,
                        onCheckedChange = { isReadingMode = !isReadingMode },
                        thumbContent = {
                            val icon = if (isReadingMode) Icons.Default.LocalLibrary
                            else Icons.Default.Draw
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize)
                            )
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.outline,
                            checkedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            checkedIconColor = MaterialTheme.colorScheme.surfaceVariant,
                            checkedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedIconColor = MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    )

                    Text(
                        text = if (isReadingMode) strings.itemReadingTitle else strings.itemWritingTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraLight
                    )

                }

                IndicatorTextRow(
                    color = MaterialTheme.colorScheme.outline,
                    startText = strings.itemTotal,
                    endText = studyProgress.total.toString(),
                    onClick = {}
                )

                IndicatorTextRow(
                    color = MaterialTheme.extraColorScheme.success,
                    startText = strings.itemDone,
                    endText = studyProgress.known.toString(),
                    onClick = {}
                )

                IndicatorTextRow(
                    color = customOrange,
                    startText = strings.itemReview,
                    endText = studyProgress.review.toString(),
                    onClick = {}
                )

                IndicatorTextRow(
                    color = customBlue,
                    startText = strings.itemNew,
                    endText = studyProgress.new.toString(),
                    onClick = {}
                )

            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentSize()
                    .size(120.dp)
            ) {

                PieIndicator(
                    modifier = Modifier.fillMaxSize(),
                    data = listOf(
                        MaterialTheme.extraColorScheme.success to studyProgress.known,
                        customOrange to studyProgress.review,
                        customBlue to studyProgress.new,
                    )
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Light,
                                fontSize = 14.sp,
                            )
                        ) { append(strings.itemGraphProgressTitle) }
                        append("\n")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 22.sp
                            )
                        ) { append(strings.itemGraphProgressValue(studyProgress.completionPercentage)) }
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )

            }

        }

        Text(text = strings.itemQuickPracticeTitle, style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            val buttonColor = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FilledTonalButton(
                onClick = {
                    quickPractice(
                        if (isReadingMode) MainDestination.Practice.Reading(
                            data.practiceId,
                            studyProgress.quickLearn
                        ) else MainDestination.Practice.Writing(
                            data.practiceId,
                            studyProgress.quickLearn
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                colors = buttonColor,
                enabled = studyProgress.quickLearn.isNotEmpty()
            ) {
                Text(strings.itemQuickPracticeLearn(studyProgress.quickLearn.size))
            }

            FilledTonalButton(
                onClick = {
                    quickPractice(
                        if (isReadingMode) MainDestination.Practice.Reading(
                            data.practiceId,
                            studyProgress.quickReview
                        ) else MainDestination.Practice.Writing(
                            data.practiceId,
                            studyProgress.quickReview,
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                colors = buttonColor,
                enabled = studyProgress.quickReview.isNotEmpty()
            ) {
                Text(strings.itemQuickPracticeReview(studyProgress.quickReview.size))
            }

        }

    }

}

@Composable
private fun IndicatorTextRow(
    color: Color,
    startText: String,
    endText: String,
    onClick: () -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp)
    ) {

        Box(
            modifier = Modifier
                .alignBy { it.measuredHeight }
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                    )
                ) { append(startText) }
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 22.sp
                    )
                ) { append(" $endText") }
            },
            modifier = Modifier.alignByBaseline()
        )
    }

}

@Composable
private fun PieIndicator(
    data: List<Pair<Color, Int>>,
    modifier: Modifier = Modifier,
) {

    val totalValue = data.sumOf { (_, value) -> value }.toFloat()
    val emptyColor = MaterialTheme.extraColorScheme.success

    Canvas(
        modifier = modifier
    ) {

        val strokeWidth = 10.dp.toPx()
        val strokeStyle = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
        val arcOffset = Offset(strokeWidth, strokeWidth).div(2f)

        if (totalValue == 0f) {
            drawArc(
                size = arcSize,
                topLeft = arcOffset,
                color = emptyColor,
                startAngle = 270f,
                sweepAngle = 360f,
                useCenter = false,
                style = strokeStyle
            )
            return@Canvas
        }

        var accumulator = 0
        data.forEach { (color, value) ->
            drawArc(
                size = arcSize,
                topLeft = arcOffset,
                color = color,
                startAngle = 270f + accumulator / totalValue * 360,
                sweepAngle = value / totalValue * 360,
                useCenter = false,
                style = strokeStyle
            )
            accumulator += value
        }

    }
}

@Composable
private fun DailyIndicator(
    state: State<DailyIndicatorData?>,
    updateConfiguration: (DailyGoalConfiguration) -> Unit
) {
    CompositionLocalProvider(
        LocalRippleTheme provides CustomRippleTheme(
            colorProvider = { MaterialTheme.colorScheme.onSurface }
        )
    ) {

        val cachedData = remember { mutableStateOf<DailyIndicatorData?>(null) }

        LaunchedEffect(Unit) {
            snapshotFlow { state.value }
                .filterNotNull()
                .onEach { cachedData.value = it }
                .collect()
        }

        val data = cachedData.value
        val strings = resolveString { practiceDashboard }
        val message = when {
            data == null -> null
            data.progress is DailyProgress.Disabled -> buildAnnotatedString {
                withStyle(SpanStyle(MaterialTheme.colorScheme.onSurface)) {
                    append(strings.dailyIndicatorPrefix)
                }
                withStyle(SpanStyle(MaterialTheme.colorScheme.outline)) {
                    append(strings.dailyIndicatorDisabled)
                }
            }
            data.progress is DailyProgress.Completed -> buildAnnotatedString {
                withStyle(SpanStyle(MaterialTheme.colorScheme.onSurface)) {
                    append(strings.dailyIndicatorPrefix)
                }
                withStyle(SpanStyle(MaterialTheme.extraColorScheme.success)) {
                    append(strings.dailyIndicatorCompleted)
                }
            }

            data.progress is DailyProgress.StudyAndReview -> buildAnnotatedString {
                withStyle(SpanStyle(MaterialTheme.colorScheme.onSurface)) {
                    append(strings.dailyIndicatorPrefix)
                }
                withStyle(SpanStyle(MaterialTheme.extraColorScheme.success)) {
                    append(strings.dailyIndicatorNew(data.progress.study))
                }
                withStyle(SpanStyle(MaterialTheme.colorScheme.onSurface)) {
                    append(" • ")
                }
                withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                    append(strings.dailyIndicatorReview(data.progress.review))
                }
            }

            data.progress is DailyProgress.StudyOnly -> buildAnnotatedString {
                withStyle(SpanStyle(MaterialTheme.colorScheme.onSurface)) {
                    append(strings.dailyIndicatorPrefix)
                }
                withStyle(SpanStyle(MaterialTheme.extraColorScheme.success)) {
                    append(strings.dailyIndicatorNew(data.progress.count))
                }
            }

            data.progress is DailyProgress.ReviewOnly -> buildAnnotatedString {
                withStyle(SpanStyle(MaterialTheme.colorScheme.onSurface)) {
                    append(strings.dailyIndicatorPrefix)
                }
                withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                    append(strings.dailyIndicatorReview(data.progress.count))
                }
            }

            else -> throw IllegalStateException()
        }

        val alpha = animateFloatAsState(
            targetValue = if (message != null) 1f else 0f
        )

        var shouldShowDialog by remember { mutableStateOf(false) }
        if (shouldShowDialog) {
            DailyGoalDialog(
                configuration = data!!.configuration,
                onDismissRequest = { shouldShowDialog = false },
                onUpdateConfiguration = {
                    updateConfiguration(it)
                    shouldShowDialog = false
                }
            )
        }

        TextButton(
            onClick = { shouldShowDialog = true },
            modifier = Modifier.fillMaxWidth().wrapContentSize().alpha(alpha.value)
        ) {
            Text(
                text = message ?: AnnotatedString("Placeholder"),
                fontWeight = FontWeight.Light
            )
        }

    }
}
