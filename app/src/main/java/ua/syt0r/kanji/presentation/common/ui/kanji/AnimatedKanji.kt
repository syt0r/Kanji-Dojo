package ua.syt0r.kanji.presentation.common.ui.kanji

import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import java.util.*

private const val initialEffectKey = "init"

@Composable
fun AnimatedKanji(
    strokes: List<Path>,
    modifier: Modifier = Modifier,
    strokeColor: Color = defaultStrokeColor(),
    strokeWidth: Float = StrokeWidth
) {

    val strokesToDraw = remember { mutableStateOf(strokes) }
    val lastStrokeAnimationProgress = remember { Animatable(1f) }

    val pathMeasure = remember {
        PathMeasure().apply { setPath(strokes.last(), false) }
    }

    val effectKey = remember { mutableStateOf(initialEffectKey) }

    LaunchedEffect(effectKey.value) {
        if (effectKey.value == initialEffectKey)
            return@LaunchedEffect

        for (strokesCount in 1..strokes.size) {
            val paths = strokes.subList(0, strokesCount)

            pathMeasure.setPath(paths.last(), false)
            strokesToDraw.value = paths

            lastStrokeAnimationProgress.snapTo(0f)
            lastStrokeAnimationProgress.animateTo(1f, tween(600))
        }
    }

    Box(
        modifier = modifier.clickable {
            effectKey.value = UUID
                .randomUUID()
                .toString()
        }
    ) {

        Canvas(
            modifier = modifier
        ) {
            val (width, height) = drawContext.size.run { width to height }
            scale(width / KanjiSize, height / KanjiSize, Offset.Zero) {
                clipRect {

                    val alpha = if (effectKey.value == initialEffectKey) 0.5f else strokeColor.alpha

                    strokesToDraw.value.dropLast(1).forEach {

                        drawPath(
                            path = it,
                            color = strokeColor,
                            alpha = alpha,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round
                            )
                        )

                    }

                    strokesToDraw.value.lastOrNull()?.also {

                        drawPath(
                            path = it,
                            color = strokeColor,
                            alpha = alpha,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(
                                        lastStrokeAnimationProgress.value * pathMeasure.length,
                                        Float.MAX_VALUE
                                    )
                                )
                            )
                        )
                    }

                }
            }
        }

        if (effectKey.value == initialEffectKey)
            Icon(
                Icons.Default.PlayArrow, null,
                Modifier
                    .size(60.dp)
                    .align(Alignment.Center)
            )

    }

}

@Composable
fun AnimatedStroke(
    stroke: Path,
    @FloatRange(from = 0.0, to = 1.0) drawProgress: () -> Float,
    modifier: Modifier = Modifier,
    strokeColor: Color = defaultStrokeColor(),
    strokeWidth: Float = StrokeWidth,
    @FloatRange(from = 0.0, to = 1.0) strokeAlpha: () -> Float = { 1f }
) {

    val strokeLength = remember(stroke) {
        PathMeasure()
            .apply { setPath(stroke, false) }
            .length
    }

    Canvas(
        modifier = modifier
    ) {
        drawStroke(
            path = stroke,
            strokeWidth = strokeWidth,
            color = { strokeColor },
            alpha = strokeAlpha,
            drawnLength = { drawProgress() * strokeLength }
        )
    }

}


fun DrawScope.drawStroke(
    path: Path,
    strokeWidth: Float,
    color: () -> Color,
    alpha: () -> Float,
    drawnLength: () -> Float
) {
    val (width, height) = drawContext.size.run { width to height }
    scale(width / KanjiSize, height / KanjiSize, Offset.Zero) {
        clipRect {
            drawPath(
                path = path,
                color = color(),
                alpha = alpha(),
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(drawnLength(), Float.MAX_VALUE)
                    )
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedKanjiPreview() {

    AppTheme {
        AnimatedKanji(
            strokes = PreviewKanji.strokes,
            modifier = Modifier.size(200.dp)
        )
    }

}
