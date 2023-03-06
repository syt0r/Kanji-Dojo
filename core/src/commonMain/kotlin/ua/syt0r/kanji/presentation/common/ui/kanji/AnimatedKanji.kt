package ua.syt0r.kanji.presentation.common.ui.kanji

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedKanji(
    strokes: List<Path>,
    modifier: Modifier = Modifier,
    strokeColor: Color = defaultStrokeColor(),
    strokeWidth: Float = StrokeWidth
) {

    val strokesToDraw = remember { mutableStateOf(strokes) }
    val lastStrokeAnimationProgress = remember { Animatable(1f) }

    var clicksCount by remember { mutableStateOf(0) }
    val strokeAlpha = if (clicksCount == 0) 0.5f else strokeColor.alpha

    LaunchedEffect(clicksCount) {
        if (clicksCount == 0)
            return@LaunchedEffect

        for (strokesCount in 1..strokes.size) {
            val paths = strokes.subList(0, strokesCount)
            strokesToDraw.value = paths

            lastStrokeAnimationProgress.snapTo(0f)
            lastStrokeAnimationProgress.animateTo(1f, tween(600))
        }
    }

    Box(
        modifier = modifier.clickable { clicksCount++ }
    ) {

        Canvas(
            modifier = modifier
        ) {

            clipRect {

                val strokesList = strokesToDraw.value

                strokesList.dropLast(1).forEach {
                    drawKanjiStroke(
                        path = it,
                        color = strokeColor.copy(strokeAlpha),
                        width = strokeWidth
                    )
                }

                strokesList.lastOrNull()?.also {

                    val pathMeasure = PathMeasure().apply { setPath(it, false) }

                    drawKanjiStroke(
                        path = it,
                        color = strokeColor.copy(strokeAlpha),
                        width = strokeWidth,
                        drawProgress = lastStrokeAnimationProgress.value
                    )
                }

            }
        }

        if (clicksCount == 0)
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
    drawProgress: () -> Float,
    modifier: Modifier = Modifier,
    strokeColor: Color = defaultStrokeColor(),
    strokeWidth: Float = StrokeWidth,
    strokeAlpha: () -> Float = { 1f }
) {

    Canvas(
        modifier = modifier
    ) {

        drawKanjiStroke(
            path = stroke,
            width = strokeWidth,
            color = strokeColor.copy(alpha = strokeAlpha()),
            drawProgress = drawProgress()
        )

    }

}