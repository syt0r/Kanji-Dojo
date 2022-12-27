package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui

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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.theme.successColor
import ua.syt0r.kanji.presentation.common.ui.kanji.*
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.DrawData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.DrawResult
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewUserAction

private const val CharacterMistakesToRepeat = 3

data class WritingPracticeInputSectionData(
    val strokes: List<Path>,
    val drawnStrokesCount: Int,
    val isStudyMode: Boolean,
    val totalMistakes: Int
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WritingPracticeInputSection(
    state: State<WritingPracticeInputSectionData>,
    onStrokeDrawn: suspend (DrawData) -> DrawResult,
    onAnimationCompleted: (DrawResult) -> Unit,
    onHintClick: () -> Unit,
    onNextClick: (ReviewUserAction) -> Unit,
    modifier: Modifier = Modifier
) {

    val hintClickCounterResetKey by remember {
        derivedStateOf { state.value.run { strokes to isStudyMode } }
    }
    val hintClickCounter = remember(hintClickCounterResetKey) { mutableStateOf(0) }

    InputDecorations(modifier = modifier) {

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

            val drawnStrokesState = remember(state.drawnStrokesCount) {
                derivedStateOf { state.run { strokes.take(drawnStrokesCount) } }
            }

            Kanji(
                strokes = drawnStrokesState,
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

            val correctlyDrawnStroke = remember { mutableStateOf<DrawResult.Correct?>(null) }
            correctlyDrawnStroke.value?.let {
                CorrectMovingStroke(
                    fromStroke = it.userDrawnPath,
                    toStroke = it.kanjiPath,
                    onAnimationEnd = {
                        onAnimationCompleted(it)
                        correctlyDrawnStroke.value = null
                    }
                )
            }

            val shouldShowStrokeInput by remember(state.drawnStrokesCount) {
                derivedStateOf { state.run { strokes.size > drawnStrokesCount } }
            }

            if (shouldShowStrokeInput) {
                StrokeInput(
                    onUserPathDrawn = { drawnPath ->
                        val result = onStrokeDrawn(DrawData(drawnPath))
                        when (result) {
                            is DrawResult.Correct -> correctlyDrawnStroke.value = result
                            is DrawResult.Mistake -> drawnMistakes = drawnMistakes.plus(result)
                            DrawResult.IgnoreCompletedPractice -> {}
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
                    onHintClick()
                    hintClickCounter.value = hintClickCounter.value + 1
                }
            ) {
                Icon(painterResource(R.drawable.ic_baseline_help_outline_24), null)
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
                        text = stringResource(R.string.writing_practice_next_button),
                        icon = Icons.Default.KeyboardArrowRight,
                        contentColor = MaterialTheme.colorScheme.surfaceVariant,
                        backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = { onNextClick(ReviewUserAction.StudyNext) }
                    )
                } else {

                    StyledTextButton(
                        text = stringResource(R.string.writing_practice_repeat_button),
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
                            text = stringResource(R.string.writing_practice_next_button),
                            icon = Icons.Default.KeyboardArrowRight,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            backgroundColor = MaterialTheme.colorScheme.successColor(),
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

    LaunchedEffect(Unit) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f)
        onAnimationEnd()
    }

    AnimatedStroke(
        fromPath = fromStroke,
        toPath = toStroke,
        progress = { animationProgress.value },
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
private fun Preview(
    useDarkTheme: Boolean = false,
    isStudyMode: Boolean = false,
    mistakes: Int = 0
) {
    AppTheme(useDarkTheme = useDarkTheme) {
        WritingPracticeInputSection(
            state = rememberUpdatedState(
                WritingPracticeInputSectionData(
                    strokes = PreviewKanji.strokes,
                    drawnStrokesCount = PreviewKanji.strokes.size,
                    isStudyMode = isStudyMode,
                    totalMistakes = mistakes
                )
            ),
            onStrokeDrawn = { TODO() },
            onAnimationCompleted = {},
            onHintClick = {},
            onNextClick = {},
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
                .size(300.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DarkModeReviewPreview() {
    Preview(useDarkTheme = true, isStudyMode = false, mistakes = 3)
}

@Preview(showBackground = true)
@Composable
private fun LightModeStudyPreview() {
    Preview(useDarkTheme = false, isStudyMode = true)
}

@Preview(showBackground = true)
@Composable
private fun DarkModeStudyPreview() {
    Preview(true, isStudyMode = true)
}