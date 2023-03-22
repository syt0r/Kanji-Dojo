package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ua.syt0r.kanji.presentation.common.resources.icon.ExtraIcons
import ua.syt0r.kanji.presentation.common.resources.icon.Help
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.ui.kanji.*
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.*

private const val CharacterMistakesToRepeat = 3

data class WritingPracticeInputSectionData(
    val strokes: List<Path>,
    val drawnStrokesCount: Int,
    val isStudyMode: Boolean,
    val totalMistakes: Int
)

@Composable
fun State<WritingReviewData>.asInputSectionState(): State<WritingPracticeInputSectionData> {
    return remember {
        derivedStateOf(structuralEqualityPolicy()) {
            value.run {
                WritingPracticeInputSectionData(
                    strokes = characterData.strokes,
                    drawnStrokesCount = drawnStrokesCount,
                    isStudyMode = isStudyMode,
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

        val hintClickCounterResetKey by remember { derivedStateOf { state.value.drawnStrokesCount } }
        val hintClickCounter = remember(hintClickCounterResetKey) { mutableStateOf(0) }

        val transition = updateTransition(
            targetState = state.value,
            label = "Different Stokes transition"
        )
        transition.AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            contentKey = { it.strokes to it.isStudyMode },
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = fadeIn(),
                    initialContentExit = fadeOut()
                )
            }
        ) { state ->

            var pendingAnimations by remember {
                mutableStateOf<Set<StrokeProcessingResult>>(emptySet())
            }

            val pendingCorrectAnimationsCount = pendingAnimations
                .filterIsInstance<StrokeProcessingResult.Correct>()
                .size

            Kanji(
                strokes = state.strokes.take(
                    state.drawnStrokesCount - pendingCorrectAnimationsCount
                ),
                modifier = Modifier.fillMaxSize()
            )

            when {
                state.isStudyMode && state.strokes.size > state.drawnStrokesCount -> {
                    StudyStroke(
                        path = state.strokes[state.drawnStrokesCount],
                        hintClicksCount = hintClickCounter,
                        delayAnimation = state.drawnStrokesCount == 0 && hintClickCounter.value == 0
                    )
                }
                hintClickCounter.value > 0 -> {
                    HintStroke(
                        path = state.strokes[state.drawnStrokesCount],
                        hintClicksCountState = hintClickCounter,
                        onAnimationEnd = { hintClickCounter.value = 0 }
                    )
                }
            }

            pendingAnimations.forEach {
                key(it) {
                    when (it) {
                        is StrokeProcessingResult.Correct -> {
                            CorrectMovingStroke(
                                fromStroke = it.userPath,
                                toStroke = it.kanjiPath,
                                onAnimationEnd = {
                                    pendingAnimations = pendingAnimations.minus(it)
                                }
                            )
                        }
                        is StrokeProcessingResult.Mistake -> {
                            ErrorFadeOutStroke(
                                path = it.hintStroke,
                                onAnimationEnd = {
                                    pendingAnimations = pendingAnimations.minus(it)
                                }
                            )
                        }
                    }
                }
            }

            val shouldShowStrokeInput by remember(state.drawnStrokesCount) {
                derivedStateOf { state.run { strokes.size > drawnStrokesCount } }
            }

            if (shouldShowStrokeInput) {
                StrokeInput(
                    onUserPathDrawn = { drawnPath ->
                        val result = onStrokeDrawn(StrokeInputData(drawnPath))
                        pendingAnimations = pendingAnimations.plus(result)
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
                    onHintClick()
                    hintClickCounter.value = hintClickCounter.value + 1
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
                        text = resolveString { writingPractice.nextButton },
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
    path: Path,
    hintClicksCountState: State<Int>,
    onAnimationEnd: () -> Unit
) {

    val strokeDrawProgress = remember { Animatable(initialValue = 0f) }
    val strokeAlpha = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(hintClicksCountState.value) {

        strokeAlpha.snapTo(1f)
        strokeDrawProgress.snapTo(0f)

        strokeDrawProgress.animateTo(1f)
        strokeAlpha.animateTo(0f)

        onAnimationEnd()

    }

    AnimatedStroke(
        stroke = path,
        modifier = Modifier.fillMaxSize(),
        strokeColor = MaterialTheme.colorScheme.error,
        drawProgress = { strokeDrawProgress.value },
        strokeAlpha = { strokeAlpha.value }
    )

}

@Composable
fun ErrorFadeOutStroke(
    path: Path,
    onAnimationEnd: () -> Unit
) {

    val strokeAlpha = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(path) {
        strokeAlpha.snapTo(1f)
        strokeAlpha.animateTo(0f, tween(600))
        onAnimationEnd()
    }

    AnimatedStroke(
        stroke = path,
        modifier = Modifier.fillMaxSize(),
        strokeColor = MaterialTheme.colorScheme.error,
        drawProgress = { 1f },
        strokeAlpha = { strokeAlpha.value }
    )

}

@Composable
fun CorrectMovingStroke(
    fromStroke: Path,
    toStroke: Path,
    onAnimationEnd: () -> Unit
) {

    val strokeLength = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(Unit) {
        strokeLength.snapTo(0f)
        strokeLength.animateTo(1f)
        onAnimationEnd()
    }

    AnimatedStroke(
        fromPath = fromStroke,
        toPath = toStroke,
        progress = { strokeLength.value },
        modifier = Modifier.fillMaxSize()
    )

}

@Composable
private fun StudyStroke(
    path: Path,
    hintClicksCount: State<Int>,
    delayAnimation: Boolean
) {

    val animationResetKey = path to hintClicksCount.value

    val strokeDrawProgress = remember(animationResetKey) { Animatable(initialValue = 0f) }

    LaunchedEffect(animationResetKey) {
        strokeDrawProgress.snapTo(0f)
        if (delayAnimation) delay(300)
        strokeDrawProgress.animateTo(1f, tween(600))
    }

    AnimatedStroke(
        stroke = path,
        modifier = Modifier.fillMaxSize(),
        strokeColor = defaultStrokeColor(),
        drawProgress = { strokeDrawProgress.value },
        strokeAlpha = { 0.5f }
    )

}
