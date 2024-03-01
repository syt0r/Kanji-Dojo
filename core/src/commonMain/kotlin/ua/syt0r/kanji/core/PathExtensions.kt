package ua.syt0r.kanji.core

import androidx.compose.ui.geometry.Offset
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

sealed interface PathApproximation {

    data class Success(
        val length: Float,
        val pathPoints: List<PathPointF>
    ) : PathApproximation {
        val points by lazy { pathPoints.map { PointF(it.x, it.y) } }
    }

    object Fail : PathApproximation

}

fun Path.approximateEvenly(pointsCount: Int): PathApproximation {
    val pathMeasure = PathMeasure()
    pathMeasure.setPath(this, false)

    val pathLength = pathMeasure.length

    // divide path into pointsCount-1 segments, store all starting points of all segments and
    // the end point of the last segment.
    val points = (0 until pointsCount).map {
        val fraction = it.toFloat() / (pointsCount - 1) * pathLength
        val position = pathMeasure.getPosition(min(fraction, pathLength))

        if (position == Offset.Unspecified)
            return PathApproximation.Fail

        PathPointF(fraction, position.x, position.y)
    }

    return PathApproximation.Success(
        length = pathLength,
        pathPoints = points
    )
}

fun Path.approximateEquidistant(distance: Float): PathApproximation {
    val pathMeasure = PathMeasure()
    pathMeasure.setPath(this, false)

    val pathLength = pathMeasure.length

    // less than "distance" pixels at the end are discarded due to rounding
    val nIntervals = max(1, (pathLength / distance).toInt())

    // divide path into nIntervals segments, store all starting points of all segments and
    // the end point of the last segment.
    val points = (0 until nIntervals + 1).map {
        val fraction = it.toFloat() / nIntervals.toFloat() * pathLength
        val point = pathMeasure.getPosition(min(fraction, pathLength))
        PathPointF(fraction, point.x, point.y)
    }

    return PathApproximation.Success(
        length = pathLength,
        pathPoints = points
    )
}

fun List<PointF>.center(): PointF {
    return PointF(
        sumOf { it.x.toDouble() / size }.toFloat(),
        sumOf { it.y.toDouble() / size }.toFloat()
    )
}

fun List<PointF>.decreaseAll(value: PointF): List<PointF> {
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

private const val INTERPOLATION_POINTS = 1 + 195

fun Path.lerpTo(
    target: Path,
    fraction: Float
): Path {
    val targetApproximation = target
        .approximateEvenly(INTERPOLATION_POINTS) as? PathApproximation.Success
        ?: return target

    val initialPathMeasure = PathMeasure()
    initialPathMeasure.setPath(this, false)
    val initialPathLength = initialPathMeasure.length

    val interpolatedCoordinates = targetApproximation.pathPoints.map { targetPathPoint ->
        initialPathMeasure.getPosition(
            targetPathPoint.fraction / targetApproximation.length * initialPathLength
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