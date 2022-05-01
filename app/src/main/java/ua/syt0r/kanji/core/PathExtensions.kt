package ua.syt0r.kanji.core

import android.graphics.PathMeasure
import android.graphics.PointF
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import java.lang.Float.min

private const val INTERPOLATION_POINTS = 21

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
    pointsCount: Int = INTERPOLATION_POINTS
): List<PointF> {
    val pathMeasure = PathMeasure()
    val androidPath = asAndroidPath()
    pathMeasure.setPath(androidPath, false)

    val pathLength = pathMeasure.length

    return (0 until pointsCount + 1).map {
        pathMeasure.pointAt(min(pathLength * it / pointsCount, pathLength))
    }
}

class PathPointF(
    val fraction: Float,
    val x: Float,
    val y: Float
)

fun Path.approximate(): List<PathPointF> {
    return asAndroidPath().approximate(1f)
        .asIterable()
        .chunked(3)
        .map { PathPointF(it[0], it[1], it[2]) }
}

fun Path.lerpBetween(initial: Path, target: Path, lerp: Float) {
    val targetPoints = target.approximate()
    val targetPathLength = targetPoints.last().fraction

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
