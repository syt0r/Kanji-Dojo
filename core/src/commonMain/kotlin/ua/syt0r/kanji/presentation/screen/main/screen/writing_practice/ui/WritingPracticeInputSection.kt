package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ua.syt0r.kanji.presentation.common.resources.icon.ExtraIcons
import ua.syt0r.kanji.presentation.common.resources.icon.Help
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.ui.kanji.AnimatedStroke
import ua.syt0r.kanji.presentation.common.ui.kanji.Kanji
import ua.syt0r.kanji.presentation.common.ui.kanji.KanjiBackground
import ua.syt0r.kanji.presentation.common.ui.kanji.StrokeInput
import ua.syt0r.kanji.presentation.common.ui.kanji.defaultStrokeColor
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewUserAction
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.StrokeInputData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.StrokeProcessingResult
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewCharacterDetails
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewData
import kotlin.math.max

private const val CharacterMistakesToRepeat = 3

data class WritingPracticeInputSectionData(
    val characterData: WritingReviewCharacterDetails,
    val isStudyMode: Boolean,
    val drawnStrokesCount: State<Int>,
    val currentStrokeMistakes: State<Int>,
    val currentCharacterMistakes: State<Int>
)

@Composable
fun State<WritingReviewData>.asInputSectionState(): State<WritingPracticeInputSectionData> {
    return remember {
        derivedStateOf {
            value.run {
                WritingPracticeInputSectionData(
                    characterData = characterData,
                    isStudyMode = isStudyMode,
                    drawnStrokesCount = drawnStrokesCount,
                    currentStrokeMistakes = currentStrokeMistakes,
                    currentCharacterMistakes = currentCharacterMistakes
                )
            }
        }
    }
}

@Composable
fun WritingPracticeInputSection(
    state: State<WritingPracticeInputSectionData>,
    onStrokeDrawn: suspend (StrokeInputData) -> StrokeProcessingResult,
    onHintClick: () -> Unit,
    onNextClick: (ReviewUserAction) -> Unit,
    modifier: Modifier = Modifier
) {

    InputDecorations(modifier = modifier) {

        val coroutineScope = rememberCoroutineScope()
        val hintClicksSharedFlow = remember { MutableSharedFlow<Unit>() }

        val newCharacterTransition = updateTransition(
            targetState = state.value,
            label = "Different Stokes transition"
        )
        newCharacterTransition.AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = fadeIn(),
                    initialContentExit = fadeOut()
                )
            }
        ) { state ->

            val isAnimatingCorrectStroke = remember { mutableStateOf(false) }
            val correctStrokeAnimations = remember { Channel<StrokeProcessingResult.Correct>() }

            val mistakeStrokeAnimations = remember { Channel<StrokeProcessingResult.Mistake>() }

            val adjustedDrawnStrokesCount = remember {
                derivedStateOf {
                    max(
                        a = 0,
                        b = state.drawnStrokesCount.value -
                                if (isAnimatingCorrectStroke.value) 1 else 0
                    )
                }
            }

            Kanji(
                strokes = state.characterData.strokes.take(adjustedDrawnStrokesCount.value),
                modifier = Modifier.fillMaxSize()
            )

            when (state.isStudyMode) {
                true -> {
                    StudyStroke(
                        strokes = state.characterData.strokes,
                        drawnStrokesCount = adjustedDrawnStrokesCount,
                        hintClicksFlow = hintClicksSharedFlow
                    )
                }

                false -> {
                    HintStroke(
                        inputState = state,
                        hintClicksFlow = hintClicksSharedFlow
                    )
                }
            }

            ErrorFadeOutStroke(
                data = remember { mistakeStrokeAnimations.consumeAsFlow() },
                onAnimationEnd = { }
            )

            CorrectMovingStroke(
                data = remember { correctStrokeAnimations.consumeAsFlow() },
                onAnimationEnd = { isAnimatingCorrectStroke.value = false }
            )

            val shouldShowStrokeInput by remember {
                derivedStateOf { state.characterData.strokes.size > state.drawnStrokesCount.value }
            }

            if (shouldShowStrokeInput) {
                StrokeInput(
                    onUserPathDrawn = { drawnPath ->
                        val result = onStrokeDrawn(
                            StrokeInputData(
                                userPath = drawnPath,
                                kanjiPath = state.characterData.strokes[state.drawnStrokesCount.value]
                            )
                        )
                        when (result) {
                            is StrokeProcessingResult.Correct -> {
                                correctStrokeAnimations.trySend(result)
                                isAnimatingCorrectStroke.value = true
                            }

                            is StrokeProcessingResult.Mistake -> {
                                mistakeStrokeAnimations.trySend(result)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

        }

        val showHintButton by derivedStateOf {
            state.value.run { drawnStrokesCount.value < characterData.strokes.size }
        }
        AnimatedVisibility(
            visible = showHintButton,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        onHintClick()
                        hintClicksSharedFlow.emit(Unit)
                    }
                }
            ) {
                Icon(ExtraIcons.Help, null)
            }
        }

        val buttonsSectionData = remember {
            derivedStateOf {
                state.value.run {
                    ButtonsSectionData(
                        visible = drawnStrokesCount.value == characterData.strokes.size,
                        isStudy = isStudyMode,
                        showNextButton = currentCharacterMistakes.value < CharacterMistakesToRepeat
                    )
                }
            }
        }

        val buttonsTransition = updateTransition(buttonsSectionData.value)
        buttonsTransition.AnimatedContent(
            transitionSpec = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up) + fadeIn() togetherWith
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down) + fadeOut()
            },
            contentKey = { it.visible },
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {

                if (!it.visible) return@AnimatedContent

                if (it.isStudy) {
                    StyledTextButton(
                        text = resolveString { writingPractice.studyFinishedButton },
                        icon = Icons.Default.KeyboardArrowRight,
                        contentColor = MaterialTheme.colorScheme.surfaceVariant,
                        backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = { onNextClick(ReviewUserAction.StudyNext) }
                    )
                } else {

                    StyledTextButton(
                        text = resolveString { writingPractice.repeatButton },
                        icon = Icons.Default.Refresh,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        onClick = { onNextClick(ReviewUserAction.Repeat) }
                    )

                    if (it.showNextButton) {
                        Spacer(modifier = Modifier.width(16.dp))
                        StyledTextButton(
                            text = resolveString { writingPractice.nextButton },
                            icon = Icons.Default.KeyboardArrowRight,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            backgroundColor = MaterialTheme.extraColorScheme.success,
                            onClick = { onNextClick(ReviewUserAction.Next) }
                        )
                    }

                }
            }
        }
    }
}

private data class ButtonsSectionData(
    val visible: Boolean,
    val isStudy: Boolean,
    val showNextButton: Boolean
)

@Composable
private fun StyledTextButton(
    text: String,
    icon: ImageVector,
    contentColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .padding(start = 16.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = contentColor
        )
        Spacer(modifier = Modifier.width(0.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor
        )
    }

}

@Composable
private fun InputDecorations(
    modifier: Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val inputShape = MaterialTheme.shapes.extraLarge
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = inputShape
            )
            .clip(inputShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = inputShape
            )
    ) {
        KanjiBackground(Modifier.fillMaxSize())
        content()
    }
}

@Composable
fun HintStroke(
    inputState: WritingPracticeInputSectionData,
    hintClicksFlow: Flow<Unit>
) {

    val currentState by rememberUpdatedState(inputState)

    val stroke = remember { mutableStateOf<Path?>(null, neverEqualPolicy()) }
    val strokeDrawProgress = remember { Animatable(initialValue = 0f) }
    val strokeAlpha = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(Unit) {

        hintClicksFlow.collectLatest {
            stroke.value = currentState.run {
                characterData.strokes.getOrNull(drawnStrokesCount.value)
            }

            strokeAlpha.snapTo(1f)
            strokeDrawProgress.snapTo(0f)

            strokeDrawProgress.animateTo(1f, tween(600))
            strokeAlpha.animateTo(0f)

            stroke.value = null
        }

    }

    stroke.value?.let {
        AnimatedStroke(
            stroke = it,
            modifier = Modifier.fillMaxSize(),
            strokeColor = MaterialTheme.colorScheme.error,
            drawProgress = { strokeDrawProgress.value },
            strokeAlpha = { strokeAlpha.value }
        )
    }

}

@Composable
fun ErrorFadeOutStroke(
    data: Flow<StrokeProcessingResult.Mistake>,
    onAnimationEnd: () -> Unit
) {

    val lastData = remember { mutableStateOf<StrokeProcessingResult.Mistake?>(null) }
    val strokeAlpha = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(Unit) {
        data.collect {
            lastData.value = it
            strokeAlpha.snapTo(1f)
            strokeAlpha.animateTo(0f, tween(600))
            onAnimationEnd()
        }
    }

    lastData.value?.let {
        AnimatedStroke(
            stroke = it.hintStroke,
            modifier = Modifier.fillMaxSize(),
            strokeColor = MaterialTheme.colorScheme.error,
            drawProgress = { 1f },
            strokeAlpha = { strokeAlpha.value }
        )
    }

}

@Composable
fun CorrectMovingStroke(
    data: Flow<StrokeProcessingResult.Correct>,
    onAnimationEnd: () -> Unit
) {

    val lastData = remember { mutableStateOf<StrokeProcessingResult.Correct?>(null) }
    val strokeLength = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(Unit) {
        data.collect {
            lastData.value = it
            strokeLength.snapTo(0f)
            strokeLength.animateTo(1f)
            lastData.value = null
            onAnimationEnd()
        }
    }

    lastData.value?.let {
        AnimatedStroke(
            fromPath = it.userPath,
            toPath = it.kanjiPath,
            progress = { strokeLength.value },
            modifier = Modifier.fillMaxSize()
        )
    }

}

@Composable
private fun StudyStroke(
    strokes: List<Path>,
    drawnStrokesCount: State<Int>,
    hintClicksFlow: Flow<Unit>
) {

    val stroke = remember { mutableStateOf<Path?>(null) }
    val strokeDrawProgress = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(Unit) {
        val autoStartFlow = merge(flowOf(Unit), hintClicksFlow)
        snapshotFlow { drawnStrokesCount.value }
            .combine(autoStartFlow) { a, b -> a }
            .collectLatest { drawnStrokesCount ->
                // Waits for stroke animation to complete
                stroke.value = strokes.getOrNull(drawnStrokesCount)
                strokeDrawProgress.snapTo(0f)
                if (drawnStrokesCount == 0) delay(300)
                strokeDrawProgress.animateTo(1f, tween(600))
            }

    }

    stroke.value?.let {
        AnimatedStroke(
            stroke = it,
            modifier = Modifier.fillMaxSize(),
            strokeColor = defaultStrokeColor(),
            drawProgress = { strokeDrawProgress.value },
            strokeAlpha = { 0.5f }
        )
    }

}
