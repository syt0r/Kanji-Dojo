package ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.lerpBetween
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.kanji.*
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawResult

private data class CharacterDrawingState(
    val strokes: List<Path>,
    val drawnStrokesCount: Int,
    val isStudyMode: Boolean
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WritingPracticeInputSection(
    strokes: List<Path>,
    drawnStrokesCount: Int,
    isStudyMode: Boolean,
    onStrokeDrawn: suspend (DrawData) -> DrawResult,
    onAnimationCompleted: (DrawResult) -> Unit,
    onHintClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val hintClickCounter = remember(strokes to isStudyMode) { mutableStateOf(0) }

    InputDecorations(modifier = modifier) {

        val coroutineScope = rememberCoroutineScope()

        val transition = updateTransition(
            targetState = CharacterDrawingState(
                strokes,
                drawnStrokesCount,
                isStudyMode
            ),
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

            Kanji(
                strokes = state.strokes.take(state.drawnStrokesCount),
                modifier = Modifier.fillMaxSize()
            )

            when {
                isStudyMode && state.strokes.size > state.drawnStrokesCount -> {
                    StudyStroke(
                        path = state.strokes[state.drawnStrokesCount],
                        hintClicksCount = hintClickCounter,
                        delayAnimation = state.drawnStrokesCount == 0 && hintClickCounter.value == 0
                    )
                }
                hintClickCounter.value > 0 && state.strokes.size > state.drawnStrokesCount -> {
                    HintStroke(
                        path = state.strokes[state.drawnStrokesCount],
                        onAnimationEnd = { hintClickCounter.value = 0 }
                    )
                }
            }

            var drawnMistakes: Set<DrawResult.Mistake> by remember { mutableStateOf(emptySet()) }
            drawnMistakes.forEach {
                key(it) {
                    ErrorFadeOutStroke(
                        path = it.path,
                        onAnimationEnd = {
                            onAnimationCompleted(it)
                            drawnMistakes = drawnMistakes.minus(it)
                        }
                    )
                }
            }

            var correctlyDrawnStroke: DrawResult.Correct? by remember { mutableStateOf(null) }
            correctlyDrawnStroke?.let {
                CorrectMovingStroke(
                    fromStroke = it.userDrawnPath,
                    toStroke = it.kanjiPath,
                    onAnimationEnd = {
                        onAnimationCompleted(it)
                        correctlyDrawnStroke = null
                    }
                )
            }

            if (state.strokes.size > state.drawnStrokesCount) {
                StrokeInput(
                    modifier = Modifier.fillMaxSize(),
                    coroutineScope = coroutineScope
                ) { drawnPath ->
                    val drawResult = onStrokeDrawn(DrawData(drawnPath))
                    when (drawResult) {
                        is DrawResult.Correct -> correctlyDrawnStroke = drawResult
                        is DrawResult.Mistake -> drawnMistakes = drawnMistakes.plus(drawResult)
                        DrawResult.IgnoreCompletedPractice -> {}
                    }
                }
            }

        }

        AnimatedVisibility(
            visible = strokes.size > drawnStrokesCount,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            IconButton(
                onClick = {
                    onHintClick()
                    hintClickCounter.value = hintClickCounter.value + 1
                }
            ) {
                Icon(painterResource(R.drawable.ic_baseline_help_outline_24), null)
            }
        }

        AnimatedVisibility(
            visible = strokes.size == drawnStrokesCount,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onNextClick)
                    .padding(vertical = 12.dp)
                    .padding(start = 16.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.writing_practice_next_button),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }

}

@Composable
private fun InputDecorations(
    modifier: Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val inputShape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .fillMaxSize()
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
    onAnimationEnd: () -> Unit
) {

    val strokeDrawProgress = remember { Animatable(initialValue = 0f) }
    val strokeAlpha = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(path) {

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

    val animationProgress = remember { Animatable(initialValue = 0f) }
    val path = remember { Path() }

    path.lerpBetween(
        fromStroke,
        toStroke,
        animationProgress.value
    )

    LaunchedEffect(Unit) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f)
        onAnimationEnd()
    }

    Stroke(
        path = path,
        modifier = Modifier.fillMaxSize()
    )

}

@Composable
private fun StudyStroke(
    path: Path,
    hintClicksCount: State<Int>,
    delayAnimation: Boolean
) {

    val strokeAlpha = remember(path, hintClicksCount.value) { mutableStateOf(0f) }

    val strokeDrawProgress = remember(path, hintClicksCount.value) {
        Animatable(initialValue = 0f)
    }

    LaunchedEffect(path, hintClicksCount.value) {
        if (delayAnimation) delay(300)
        strokeAlpha.value = 0.3f
        strokeDrawProgress.snapTo(0f)
        strokeDrawProgress.animateTo(1f, tween(600))
    }

    AnimatedStroke(
        stroke = path,
        modifier = Modifier.fillMaxSize(),
        strokeColor = defaultStrokeColor(),
        drawProgress = { strokeDrawProgress.value },
        strokeAlpha = { strokeAlpha.value }
    )

}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        WritingPracticeInputSection(
            strokes = PreviewKanji.strokes,
            drawnStrokesCount = PreviewKanji.strokes.size,
            isStudyMode = false,
            onStrokeDrawn = { TODO() },
            onAnimationCompleted = {},
            onHintClick = {},
            onNextClick = {},
            modifier = Modifier
                .padding(20.dp)
                .size(300.dp)
        )
    }
}