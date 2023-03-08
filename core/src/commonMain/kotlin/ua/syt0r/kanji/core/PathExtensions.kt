package ua.syt0r.kanji.core

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import kotlin.math.min

data class PointF(
    val x: Float,
    val y: Float
)

data class PathPointF(
    val fraction: Float,
    val x: Float,
    val y: Float
)

data class ApproximatedPath(
    val length: Float,
    val points: List<PathPointF>
)

expect fun PathMeasure.pointAt(fraction: Float): PointF

fun Path.approximateEvenly(pointsCount: Int): ApproximatedPath {
    val pathMeasure = PathMeasure()
    pathMeasure.setPath(this, false)

    val pathLength = pathMeasure.length
    val points = (0 until pointsCount).map {
        val fraction = it.toFloat() / pointsCount * pathLength +
                it.toFloat() / pointsCount * pathLength
        val point = pathMeasure.pointAt(min(fraction, pathLength))
        PathPointF(fraction, point.x, point.y)
    }

    return ApproximatedPath(
        length = pathLength,
        points = points
    )
}

// TODO no approximate in skia
//fun Path.approximatePrecise(length: Float): List<PathPointF> {
//    return asAndroidPath().approximate(1f)
//        .asIterable()
//        .chunked(3)
//        .map {
//            // Fraction is given from 0 to 1 here
//            PathPointF(it[0] * length, it[1], it[2])
//        }
//}

fun Path.lerpBetween(
    initial: Path,
    target: Path,
    lerp: Float
) {
    val (targetPathLength, targetPoints) = target.approximateEvenly(20)

    val initialPathMeasure = PathMeasure()
    initialPathMeasure.setPath(initial, false)
    val initialPathLength = initialPathMeasure.length

    val interpolatedCoordinates = targetPoints.map { targetPathPoint ->
        initialPathMeasure.pointAt(
            targetPathPoint.fraction / targetPathLength * initialPathLength
        ).run {
            PointF(
                x = x + (targetPathPoint.x - x) * lerp,
                y = y + (targetPathPoint.y - y) * lerp
            )
        }
    }

    reset()
    val start = interpolatedCoordinates.first()
    moveTo(start.x, start.y)

    // TODO try with quadraticBezierTo()
    interpolatedCoordinates.slice(1 until interpolatedCoordinates.size)
        .forEach { lineTo(it.x, it.y) }
}