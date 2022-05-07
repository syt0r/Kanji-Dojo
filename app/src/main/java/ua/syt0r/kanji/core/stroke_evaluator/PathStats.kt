package ua.syt0r.kanji.core.stroke_evaluator

import android.graphics.PointF
import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji.core.approximateEvenly

class PathStats(
    val evenlyApproximated: List<PointF>,
    val length: Float
)

private const val INTERPOLATION_POINTS = 22

fun Path.getStats(): PathStats {
    val points = approximateEvenly(INTERPOLATION_POINTS)
    return PathStats(
        length = points.maxOf { it.fraction },
        evenlyApproximated = points.map { PointF(it.x, it.y) }
    )
}