package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
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
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewData
import kotlin.math.max

private const val CharacterMistakesToRepeat = 3

data class WritingPracticeInputSectionData(
    val character: String,
    val strokes: List<Path>,
    val isStudyMode: Boolean,
    val drawnStrokesCount: Int,
    val totalMistakes: Int
)

@Composable
fun State<WritingReviewData>.asInputSectionState(): State<WritingPracticeInputSectionData> {
    return remember {
        derivedStateOf {
            value.run {
                WritingPracticeInputSectionData(
                    character = characterData.character,
                    strokes = characterData.strokes,
                    isStudyMode = isStudyMode,
                    drawnStrokesCount = drawnStrokesCount,
                    totalMistakes = currentCharacterMistakes
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
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

        val transition = updateTransition(
            targetState = state.value,
            label = "Different Stokes transition"
        )
        transition.AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            contentKey = { it.character to it.isStudyMode },
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = fadeIn(),
                    initialContentExit = fadeOut()
                )
            }
        ) { state ->

            val animatingCorrectStroke = remember { mutableStateOf(false) }
            val correctStrokeAnimations = remember { Channel<StrokeProcessingResult.Correct>() }

            val mistakeStrokeAnimations = remember { Channel<StrokeProcessingResult.Mistake>() }

            Kanji(
                strokes = state.strokes.take(
                    max(0, state.drawnStrokesCount - if (animatingCorrectStroke.value) 1 else 0)
                ),
                modifier = Modifier.fillMaxSize()
            )

            when (state.isStudyMode) {
                true -> {
                    StudyStroke(
                        inputState = state,
                        isAnimatingCorrectStroke = animatingCorrectStroke,
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
                onAnimationEnd = { animatingCorrectStroke.value = false }
            )

            val shouldShowStrokeInput by remember(state.drawnStrokesCount) {
                derivedStateOf { state.run { strokes.size > drawnStrokesCount } }
            }

            if (shouldShowStrokeInput) {
                StrokeInput(
                    onUserPathDrawn = { drawnPath ->
                        val result = onStrokeDrawn(StrokeInputData(drawnPath))
                        when (result) {
                            is StrokeProcessingResult.Correct -> {
                                correctStrokeAnimations.trySend(result)
                                animatingCorrectStroke.value = true
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

        transition.AnimatedVisibility(
            visible = { it.strokes.size > it.drawnStrokesCount },
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

        transition.AnimatedVisibility(
            visible = { it.strokes.size == it.drawnStrokesCount },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {

            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val isStudyMode by remember { derivedStateOf { transition.currentState.isStudyMode } }

                if (isStudyMode) {
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

                    val isTooManyMistakes by remember {
                        derivedStateOf { transition.currentState.totalMistakes >= CharacterMistakesToRepeat }
                    }

                    if (!isTooManyMistakes) {
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
            stroke.value = currentState.run { strokes.getOrNull(drawnStrokesCount) }

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
    inputState: WritingPracticeInputSectionData,
    isAnimatingCorrectStroke: State<Boolean>,
    hintClicksFlow: Flow<Unit>
) {

    val currentState = rememberUpdatedState(inputState)
    val stroke = remember { mutableStateOf<Path?>(null) }
    val strokeDrawProgress = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(Unit) {
        var previouslyDrawnCharacter = ""
        val autoStartFlow = merge(flowOf(Unit), hintClicksFlow)
        snapshotFlow { currentState.value }
            .combine(autoStartFlow) { a, b -> a }
            .transform {
                val shouldDelay = previouslyDrawnCharacter != it.character
                previouslyDrawnCharacter = it.character
                emit(it to shouldDelay)
            }
            .collectLatest { (data, shouldDelay) ->
                // Waits for stroke animation to complete
                snapshotFlow { isAnimatingCorrectStroke.value }.filter { !it }.firstOrNull()
                stroke.value = data.strokes.getOrNull(data.drawnStrokesCount)
                strokeDrawProgress.snapTo(0f)
                if (shouldDelay) delay(300)
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
