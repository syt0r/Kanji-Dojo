package ua.syt0r.kanji.core.stroke_evaluator

import android.graphics.PointF
import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji.core.approximateEvenly
import ua.syt0r.kanji.core.length

class PathStats(
    val length: Float,
    val evenlyApproximated: List<PointF>
)

fun Path.getStats(): PathStats = PathStats(
    length = length(),
    evenlyApproximated = approximateEvenly()
)