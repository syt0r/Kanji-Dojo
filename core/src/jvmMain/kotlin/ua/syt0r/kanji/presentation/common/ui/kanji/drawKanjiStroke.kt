package ua.syt0r.kanji.presentation.common.ui.kanji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale

actual fun DrawScope.drawKanjiStroke(
    path: Path,
    color: Color,
    width: Float,
    drawProgress: Float?
) {
    val scale = size.maxDimension / KanjiSize
    scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero) {
        drawPath(
            path = path,
            color = color,
            alpha = color.alpha,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = width,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = drawProgress?.let {
                    val pathLength = PathMeasure().apply { setPath(path, false) }.length
                    PathEffect.dashPathEffect(floatArrayOf(pathLength * it, Float.MAX_VALUE))
                }
            )
        )
    }
}
