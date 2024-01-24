package ua.syt0r.kanji.core

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

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

    // divide path into pointsCount-1 segments, store all starting points of all segments and
    // the end point of the last segment.
    val points = (0 until pointsCount).map {
        val fraction = it.toFloat() / (pointsCount-1) * pathLength
        val point = pathMeasure.pointAt(min(fraction, pathLength))
        PathPointF(fraction, point.x, point.y)
    }

    return ApproximatedPath(
        length = pathLength,
        points = points
    )
}

fun Path.approximateEquidistant(distance: Float): ApproximatedPath {
    val pathMeasure = PathMeasure()
    pathMeasure.setPath(this, false)

    val pathLength = pathMeasure.length

    // less than "distance" pixels at the end are discarded due to rounding
    val nIntervals = max(1, (pathLength / distance).toInt())

    // divide path into nIntervals segments, store all starting points of all segments and
    // the end point of the last segment.
    val points = (0 until  nIntervals+1).map {
        val fraction = it.toFloat() / nIntervals.toFloat() * pathLength
        val point = pathMeasure.getPosition(min(fraction, pathLength))
        PathPointF(fraction, point.x, point.y)
    }

    return ApproximatedPath(
        length = pathLength,
        points = points
    )
}

fun List<PointF>.center(): PointF {
    return PointF(
        sumOf { it.x.toDouble() / size }.toFloat(),
        sumOf { it.y.toDouble() / size }.toFloat()
    )
}

fun List<PointF>.minus(value: PointF): List<PointF> {
    return map { PointF(it.x - value.x, it.y - value.y) }
}

fun relativeScale(
    first: List<PointF>,
    second: List<PointF>
): Pair<Size, Size> {
    fun List<PointF>.getScale(): Size = Size(
        width = run { maxOf { it.x } - minOf { it.x } },
        height = run { maxOf { it.y } - minOf { it.y } }
    )

    val firstSize = first.getScale()
    val secondSize = second.getScale()

    return firstSize to secondSize
}

fun List<PointF>.scaled(scaleX: Float, scaleY: Float): List<PointF> {
    return map { PointF(it.x * scaleX, it.y * scaleY) }
}

fun euclDistance(
    pointA: PointF,
    pointB: PointF,
): Float {
    return sqrt(
        (pointA.x - pointB.x).pow(2) + (pointA.y - pointB.y).pow(2)
    )
}

class PathStats(
    val evenlyApproximated: List<PointF>,
    val length: Float
)

fun Path.getStats(interpolationPoints:Int): PathStats {
    val approximatedPath = approximateEvenly(interpolationPoints)
    return PathStats(
        length = approximatedPath.length,
        evenlyApproximated = approximatedPath.points.map { PointF(it.x, it.y) }
    )
}

fun Path.getStats(distance: Float): PathStats {
    val approximatedPath = approximateEquidistant(distance)
    return PathStats(
        length = approximatedPath.length,
        evenlyApproximated = approximatedPath.points.map { PointF(it.x, it.y) }
    )
}

private const val INTERPOLATION_POINTS = 1 + 195

fun Path.lerpTo(
    target: Path,
    fraction: Float
): Path {
    val (targetPathLength, targetPoints) = target.approximateEvenly(INTERPOLATION_POINTS)

    val initialPathMeasure = PathMeasure()
    initialPathMeasure.setPath(this, false)
    val initialPathLength = initialPathMeasure.length

    val interpolatedCoordinates = targetPoints.map { targetPathPoint ->
        initialPathMeasure.pointAt(
            targetPathPoint.fraction / targetPathLength * initialPathLength
        ).run {
            PointF(
                x = x + (targetPathPoint.x - x) * fraction,
                y = y + (targetPathPoint.y - y) * fraction
            )
        }
    }

    return Path().apply {

        val start = interpolatedCoordinates.first()
        moveTo(start.x, start.y)

        interpolatedCoordinates.drop(1)
            .chunked(3)
            .forEach { (a, b, c) -> cubicTo(a.x, a.y, b.x, b.y, c.x, c.y) }

    }
}