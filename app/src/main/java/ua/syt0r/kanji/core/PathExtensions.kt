package ua.syt0r.kanji.core

import android.graphics.PathMeasure
import android.graphics.PointF
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import java.lang.Float.min

fun Path.length(): Float {
    val pathMeasure = PathMeasure()
    pathMeasure.setPath(asAndroidPath(), false)
    return pathMeasure.length
}

fun PathMeasure.pointAt(fraction: Float): PointF {
    val position = FloatArray(2)
    getPosTan(fraction, position, null)
    return PointF(position[0], position[1])
}

fun Path.approximateEvenly(
    pointsCount: Int
): List<PathPointF> {
    val pathMeasure = PathMeasure()
    val androidPath = asAndroidPath()
    pathMeasure.setPath(androidPath, false)

    val pathLength = pathMeasure.length

    return (0 until pointsCount).map {
        val fraction = it.toFloat() / pointsCount * pathLength +
                it.toFloat() / pointsCount * pathLength
        val point = pathMeasure.pointAt(min(fraction, pathLength))
        PathPointF(fraction, point.x, point.y)
    }
}

data class PathPointF(
    val fraction: Float,
    val x: Float,
    val y: Float
)

fun Path.approximatePrecise(length: Float): List<PathPointF> {
    return asAndroidPath().approximate(1f)
        .asIterable()
        .chunked(3)
        .map {
            // Fraction is given from 0 to 1 here
            PathPointF(it[0] * length, it[1], it[2])
        }
}

fun Path.lerpBetween(initial: Path, target: Path, lerp: Float) {
    val targetPathLength = target.length()
    val targetPoints = target.approximatePrecise(targetPathLength)

    val initialPathMeasure = PathMeasure()
    initialPathMeasure.setPath(initial.asAndroidPath(), false)
    val initialPathLength = initialPathMeasure.length

    val interpolatedCoordinates = targetPoints.map { targetPathPoint ->
        initialPathMeasure.pointAt(
            targetPathPoint.fraction / targetPathLength * initialPathLength
        ).apply {
            x += (targetPathPoint.x - x) * lerp
            y += (targetPathPoint.y - y) * lerp
        }
    }

    reset()
    val start = interpolatedCoordinates.first()
    moveTo(start.x, start.y)

    interpolatedCoordinates.slice(1 until interpolatedCoordinates.size)
        .forEach { lineTo(it.x, it.y) }
}
