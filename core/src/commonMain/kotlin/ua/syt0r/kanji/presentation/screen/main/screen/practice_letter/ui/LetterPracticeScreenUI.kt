package ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.japanese.CharacterClassification
import ua.syt0r.kanji.core.japanese.KanaReading
import ua.syt0r.kanji.core.toRadians
import ua.syt0r.kanji.practice_summary_header_accuracy
import ua.syt0r.kanji.presentation.common.MultiplatformBackHandler
import ua.syt0r.kanji.presentation.common.copyCentered
import ua.syt0r.kanji.presentation.common.resolveString
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.FancyLoading
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeAnswer
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeConfigurationCharactersPreview
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeConfigurationContainer
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeConfigurationEnumSelector
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeConfigurationItemsSelector
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeConfigurationOption
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeEarlyFinishDialog
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeSummaryContainer
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeSummaryEmptyList
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeSummaryInfoLabel
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeSummaryItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeToolbar
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeToolbarState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.LetterPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeReviewState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeSummaryItem
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.WritingPracticeHintMode
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.WritingPracticeInputMode
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.roundToInt

@Composable
fun LetterPracticeScreenUI(
    state: State<ScreenState>,
    navigateBack: () -> Unit,
    navigateToWordFeedback: (JapaneseWord) -> Unit,
    onConfigured: () -> Unit,
    speakKana: (KanaReading) -> Unit,
    onNextClick: (PracticeAnswer) -> Unit,
    onWordClick: (JapaneseWord) -> Unit,
    onSummaryItemCLick: (LetterPracticeSummaryItem) -> Unit,
    finishPractice: () -> Unit,
    onPracticeCompleted: () -> Unit
) {

    var showEarlyFinishDialog by rememberSaveable { mutableStateOf(false) }
    if (showEarlyFinishDialog) {
        PracticeEarlyFinishDialog(
            onDismissRequest = { showEarlyFinishDialog = false },
            onConfirmClick = {
                showEarlyFinishDialog = false
                finishPractice()
            }
        )
    }

    val shouldShowLeaveConfirmationOnBackClick = remember {
        derivedStateOf {
            state.value.let { !(it is ScreenState.Configuring || it is ScreenState.Summary) }
        }
    }

    if (shouldShowLeaveConfirmationOnBackClick.value) {
        MultiplatformBackHandler { showEarlyFinishDialog = true }
    }

    ScreenLayout(
        state = state,
        toolbar = {
            PracticeToolbar(
                state = state.toToolbarState(),
                onUpButtonClick = {
                    if (shouldShowLeaveConfirmationOnBackClick.value) {
                        showEarlyFinishDialog = true
                    } else {
                        navigateBack()
                    }
                }
            )
        },
        configuration = {
            ConfiguringState(
                state = it,
                onConfigurationCompleted = onConfigured
            )
        },
        review = {
            ReviewState(
                reviewState = it.reviewState,
                onNextClick = onNextClick,
                speakKana = speakKana,
                onWordClick = onWordClick
            )
        },
        summary = {
            SummaryState(
                screenState = it,
                onItemClick = onSummaryItemCLick,
                onFinishClick = onPracticeCompleted
            )
        }
    )

}

@Composable
private fun ScreenLayout(
    state: State<ScreenState>,
    toolbar: @Composable () -> Unit,
    configuration: @Composable (ScreenState.Configuring) -> Unit,
    review: @Composable (ScreenState.Review) -> Unit,
    summary: @Composable (ScreenState.Summary) -> Unit
) {

    Scaffold(
        topBar = { toolbar() }
    ) { paddingValues ->

        val transition = updateTransition(targetState = state.value, label = "AnimatedContent")
        transition.AnimatedContent(
            transitionSpec = {
                fadeIn(tween(600)) togetherWith fadeOut(tween(600))
            },
            contentKey = { it is ScreenState.Review },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { screenState ->

            when (screenState) {
                ScreenState.Loading -> FancyLoading(Modifier.fillMaxSize().wrapContentSize())
                is ScreenState.Configuring -> configuration(screenState)
                is ScreenState.Review -> review(screenState)
                is ScreenState.Summary -> summary(screenState)
            }

        }

    }

}

@Composable
private fun State<ScreenState>.toToolbarState(): State<PracticeToolbarState> {
    val state = remember {
        derivedStateOf {
            when (val currentState = value) {
                ScreenState.Loading,
                is ScreenState.Summary -> PracticeToolbarState.Idle

                is ScreenState.Configuring -> PracticeToolbarState.Configuration

                is ScreenState.Review -> PracticeToolbarState.Review(
                    practiceQueueProgress = currentState.practiceProgress
                )

            }
        }
    }
    return state
}

@Composable
private fun ConfiguringState(
    state: ScreenState.Configuring,
    onConfigurationCompleted: () -> Unit
) {

    val strings = resolveString { letterPractice }

    val practiceTypeTitle = resolveString(state.configuration.practiceType.titleResolver)

    PracticeConfigurationContainer(
        onClick = onConfigurationCompleted,
        practiceTypeMessage = strings.configurationTitle(practiceTypeTitle)
    ) {

        when (val configuration = state.configuration) {
            is LetterPracticeConfiguration.Writing -> {

                var noTranslationLayout by configuration.noTranslationsLayout
                var leftHandedMode by configuration.leftHandedMode
                var kanaRomaji by configuration.useRomajiForKanaWords
                var altStrokeEvaluatorEnabled by configuration.altStrokeEvaluatorEnabled
                var selectedHintMode by configuration.hintMode
                var selectedInputMode by configuration.inputMode

                PracticeConfigurationItemsSelector(
                    state = configuration.selectorState
                )

                PracticeConfigurationCharactersPreview(
                    characters = configuration.selectorState.sortedList.value.map { it.first },
                    selectedCharactersCount = configuration.selectorState.selectedCountIntState
                )

                PracticeConfigurationEnumSelector(
                    title = resolveString { letterPractice.hintStrokesTitle },
                    subtitle = resolveString { letterPractice.hintStrokesMessage },
                    values = WritingPracticeHintMode.entries,
                    selected = selectedHintMode,
                    onSelected = { selectedHintMode = it }
                )

                PracticeConfigurationEnumSelector(
                    title = resolveString { letterPractice.inputModeTitle },
                    subtitle = resolveString { letterPractice.inputModeMessage },
                    values = WritingPracticeInputMode.entries,
                    selected = selectedInputMode,
                    onSelected = { selectedInputMode = it }
                )

                PracticeConfigurationOption(
                    title = strings.kanaRomajiTitle,
                    subtitle = strings.kanaRomajiMessage,
                    checked = kanaRomaji,
                    onChange = { kanaRomaji = it }
                )

                PracticeConfigurationOption(
                    title = strings.noTranslationLayoutTitle,
                    subtitle = strings.noTranslationLayoutMessage,
                    checked = noTranslationLayout,
                    onChange = { noTranslationLayout = it }
                )

                PracticeConfigurationOption(
                    title = strings.leftHandedModeTitle,
                    subtitle = strings.leftHandedModeMessage,
                    checked = leftHandedMode,
                    onChange = { leftHandedMode = it }
                )

                PracticeConfigurationOption(
                    title = strings.altStrokeEvaluatorTitle,
                    subtitle = strings.altStrokeEvaluatorMessage,
                    checked = altStrokeEvaluatorEnabled,
                    onChange = { altStrokeEvaluatorEnabled = it }
                )
            }

            is LetterPracticeConfiguration.Reading -> {

                PracticeConfigurationItemsSelector(
                    state = configuration.selectorState
                )

                PracticeConfigurationCharactersPreview(
                    characters = configuration.selectorState.sortedList.value.map { it.first },
                    selectedCharactersCount = configuration.selectorState.selectedCountIntState
                )

                PracticeConfigurationOption(
                    title = resolveString { letterPractice.kanaRomajiTitle },
                    subtitle = resolveString { letterPractice.kanaRomajiMessage },
                    checked = configuration.useRomajiForKanaWords.value,
                    onChange = { configuration.useRomajiForKanaWords.value = it }
                )

            }
        }

    }

}

@Composable
private fun ReviewState(
    reviewState: LetterPracticeReviewState,
    onNextClick: (PracticeAnswer) -> Unit,
    speakKana: (KanaReading) -> Unit,
    onWordClick: (JapaneseWord) -> Unit
) {

    when (reviewState) {
        is LetterPracticeReviewState.Writing -> LetterPracticeWritingUI(
            layoutConfiguration = reviewState.layout,
            reviewState = reviewState,
            onNextClick = onNextClick,
            speakKana = speakKana,
            onWordClick = onWordClick
        )

        is LetterPracticeReviewState.Reading -> LetterPracticeReadingUI(
            reviewState = reviewState,
            onNextClick = onNextClick,
            speakKana = speakKana,
            onWordClick = onWordClick
        )
    }

}

@Composable
private fun SummaryState(
    screenState: ScreenState.Summary,
    onItemClick: (LetterPracticeSummaryItem) -> Unit,
    onFinishClick: () -> Unit
) {

    PracticeSummaryContainer(
        practiceDuration = screenState.duration,
        summaryItemsCount = screenState.items.size,
        onFinishClick = onFinishClick,
        headerContent = {
            if (screenState.accuracy != null) {
                AccuracyPracticeSummaryInfoLabel(screenState.accuracy.roundToInt())
            }
        }
    ) {

        screenState.items.takeIf { it.isNotEmpty() }
            ?.forEachIndexed { index, item ->
                PracticeSummaryItem(
                    index = index,
                    header = {
                        Text(
                            text = item.letter,
                            modifier = Modifier
                        )
                    },
                    totalReviews = item.totalReviews,
                    nextInterval = item.nextInterval,
                    onClick = { onItemClick(item) }
                )
            }
            ?: PracticeSummaryEmptyList()

    }

}

@Composable
private fun RowScope.AccuracyPracticeSummaryInfoLabel(accuracy: Int) {
    PracticeSummaryInfoLabel(
        title = stringResource(Res.string.practice_summary_header_accuracy),
        data = "$accuracy%"
    )

    val mark = when {
        accuracy >= 90 -> "S"
        accuracy >= 80 -> "A"
        accuracy >= 70 -> "B"
        accuracy >= 60 -> "C"
        accuracy >= 50 -> "D"
        else -> "F"
    }

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) { animationProgress.animateTo(1f, tween(600)) }

    val markColor = MaterialTheme.colorScheme.primary

    val textMeasurer = rememberTextMeasurer()
    val markTextStyle = MaterialTheme.typography.headlineSmall.copyCentered()
    val result = textMeasurer.measure(mark, markTextStyle)
    val strokeWidth = 4.dp
    val extraPadding = 4.dp

    val sizeDp = with(LocalDensity.current) {
        result.size.toSize().maxDimension.toDp()
            .plus(extraPadding).let { DpSize(it, it) }
    }

    Canvas(
        modifier = Modifier
            .size(sizeDp)
            .align(Alignment.CenterVertically)
    ) {

        drawArc(
            color = markColor,
            startAngle = -90f,
            sweepAngle = (270f) * animationProgress.value,
            useCenter = false,
            topLeft = Offset.Zero,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        )

        drawText(
            textLayoutResult = result,
            color = markColor,
            topLeft = Offset(
                (size.width - result.size.width) / 2f,
                size.height / 2f - result.size.height / 2f,
            )
        )

    }
}

@Composable
fun KanjiVariantsRow(
    variants: String,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(text = resolveString { letterPractice.variantsTitle })

        val showVariants = remember { mutableStateOf(false) }

        val overlayAlpha = animateFloatAsState(targetValue = if (showVariants.value) 0f else 1f)

        val overlayColor = MaterialTheme.colorScheme.surfaceVariant
            .copy(alpha = overlayAlpha.value)
        val hintTextColor = MaterialTheme.colorScheme.onSurface
            .copy(alpha = overlayAlpha.value)
        val variantsTextColor = MaterialTheme.colorScheme.onSurface
            .copy(alpha = 1f - overlayAlpha.value)

        Box(
            modifier = Modifier.clip(MaterialTheme.shapes.small)
                .clickable { showVariants.value = !showVariants.value }
                .background(overlayColor)
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Text(
                text = resolveString { letterPractice.variantsHint },
                color = hintTextColor,
                maxLines = 1
            )
            Text(
                text = variants,
                color = variantsTextColor
            )
        }

    }

}

@Composable
fun LetterPracticeKanaInfo(
    kanaSystem: CharacterClassification.Kana,
    reading: KanaReading,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = buildAnnotatedString {
                append(kanaSystem.resolveString().toLowerCase(Locale.current))
                append(" ")
                append(reading.nihonShiki)
            },
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        reading.alternative?.let { alternativeReadings ->
            Text(
                text = resolveString { commonPractice.additionalKanaReadingsNote(alternativeReadings) },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

}


private const val WaveMaxFraction = 0.8
private const val WaveMinFraction = 0.4

private val WaveStrokesPhaseShifts = listOf(-90.0, -45.0, 0.0)
    .let { it + it.take(it.size - 1).reversed().map { it * -1 } }

@Composable
fun ColumnScope.KanaVoiceMenu(
    autoPlayEnabled: State<Boolean>,
    clickable: Boolean,
    onAutoPlayToggleClick: () -> Unit,
    onSpeakClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val coroutineScope = rememberCoroutineScope()
    val waveAnimationProgress = remember { Animatable(1f) }

    Row(
        modifier = modifier.align(Alignment.CenterHorizontally)
            .height(IntrinsicSize.Max)
            .width(200.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(
                enabled = clickable,
                onClick = {
                    onSpeakClick()
                    coroutineScope.launch {
                        waveAnimationProgress.snapTo(0f)
                        waveAnimationProgress.animateTo(1f, tween())
                    }
                }
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val (autoPlatTextColor, autoPlatContainerColor) = when {
            autoPlayEnabled.value -> {
                MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurfaceVariant
            }

            else -> {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f) to MaterialTheme.colorScheme.surfaceVariant
            }
        }

        Text(
            text = "A",
            color = autoPlatTextColor,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeightStyle = LineHeightStyle(
                    LineHeightStyle.Alignment.Center,
                    LineHeightStyle.Trim.Both
                )
            ),
            modifier = Modifier
                .aspectRatio(1f, true)
                .clip(CircleShape)
                .background(autoPlatContainerColor)
                .clickable(enabled = clickable, onClick = onAutoPlayToggleClick)
                .wrapContentSize(unbounded = true)
        )

        Spacer(Modifier.weight(1f))

        WaveStrokesPhaseShifts.forEach { phaseShift ->
            val phase = toRadians(-waveAnimationProgress.value * 360 - phaseShift)
            val height =
                WaveMinFraction + cos(phase).absoluteValue * (WaveMaxFraction - WaveMinFraction)
            Box(
                modifier = Modifier
                    .padding(horizontal = 1.dp)
                    .fillMaxHeight(height.toFloat())
                    .width(3.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f))
            )
        }

        Spacer(Modifier.weight(1f))

        Icon(Icons.AutoMirrored.Filled.VolumeUp, null)

    }

}
