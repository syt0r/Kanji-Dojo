package ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.lerpBetween
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.kanji.*
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WritingPracticeInputSection(
    screenState: WritingPracticeScreenContract.ScreenState.Review,
    onStrokeDrawn: suspend (DrawData) -> DrawResult,
    onAnimationCompleted: (DrawResult) -> Unit,
    onHintClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val hintClickCounter = remember(screenState.data.character) { mutableStateOf(0) }

    InputDecorations(modifier = modifier) {

        val coroutineScope = rememberCoroutineScope()

        val transition = updateTransition(targetState = screenState, label = "")
        transition.AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            contentKey = { it.data.character },
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = fadeIn(),
                    initialContentExit = fadeOut()
                )
            }
        ) { screenState ->

            var isLastCharacterStroke by remember(screenState.data.character) {
                mutableStateOf(false)
            }

            val strokesToDraw = if (isLastCharacterStroke) screenState.data.strokes.size
            else screenState.drawnStrokesCount

            Kanji(
                strokes = screenState.data.strokes.take(strokesToDraw),
                modifier = Modifier.fillMaxSize()
            )

            when (screenState.practiceMode) {
                WritingPracticeMode.Learn -> {
                    HelperStroke(
                        path = screenState.run { data.strokes[drawnStrokesCount] },
                        hintClicksCount = hintClickCounter,
                        delayAnimation = screenState.drawnStrokesCount == 0 &&
                                hintClickCounter.value == 0
                    )
                }
                WritingPracticeMode.Review -> {
                    if (hintClickCounter.value > 0) {
                        SelfDrawingStroke(
                            path = screenState.run { data.strokes[drawnStrokesCount] },
                            onAnimationEnd = { hintClickCounter.value = 0 }
                        )
                    }

                }
            }

            var drawnMistakes: Set<DrawResult.Mistake> by remember { mutableStateOf(emptySet()) }
            drawnMistakes.forEach {
                key(it) {
                    FadeOutStroke(
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
                MovingStroke(
                    fromStroke = it.userDrawnPath,
                    toStroke = it.kanjiPath,
                    onAnimationEnd = {
                        onAnimationCompleted(it)
                        val isLastStrokeDrawn = screenState.run {
                            drawnStrokesCount == data.strokes.size - 1
                        }
                        if (isLastStrokeDrawn) {
                            isLastCharacterStroke = true
                        }
                        correctlyDrawnStroke = null
                    }
                )
            }

            StrokeInput(
                modifier = Modifier.fillMaxSize(),
                coroutineScope = coroutineScope
            ) { drawnPath ->
                val drawResult = onStrokeDrawn(DrawData(drawnPath))
                when (drawResult) {
                    is DrawResult.Correct -> correctlyDrawnStroke = drawResult
                    is DrawResult.Mistake -> drawnMistakes = drawnMistakes.plus(drawResult)
                }
            }

        }

        IconButton(
            onClick = {
                onHintClick()
                hintClickCounter.value = hintClickCounter.value + 1
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Icon(painterResource(R.drawable.ic_baseline_help_outline_24), null)
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
            .aspectRatio(1f)
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
fun SelfDrawingStroke(
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
fun FadeOutStroke(
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
fun MovingStroke(
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
private fun HelperStroke(
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
            screenState = PreviewKanji.run {
                WritingPracticeScreenContract.ScreenState.Review(
                    data = ReviewCharacterData.KanaReviewData(
                        kanji,
                        strokes,
                        kanaSystem = "Hiragana",
                        romaji = "A"
                    ),
                    practiceMode = WritingPracticeMode.Learn,
                    drawnStrokesCount = 3,
                    progress = PracticeProgress(5, 1)
                )
            },
            onStrokeDrawn = { TODO() },
            onAnimationCompleted = {},
            onHintClick = {},
            modifier = Modifier
                .padding(20.dp)
                .size(200.dp)
        )
    }
}