package ua.syt0r.kanji.core.stroke_evaluator

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji.core.PointF
import ua.syt0r.kanji.core.approximateEvenly
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.common.ui.kanji.KanjiSize
import kotlin.math.*

class AltKanjiStrokeEvaluator : KanjiStrokeEvaluator {

    companion object {
        private const val SIMILARITY_ERROR_THRESHOLD = 10f
        private const val INTERPOLATION_POINTS = 22
    }

    override fun areStrokesSimilar(
        first: Path,
        second: Path
    ): Boolean {
        val error = getError(first, second)
        return error <= SIMILARITY_ERROR_THRESHOLD
    }

    private fun getError(first: Path, second: Path): Float {
        // divide into equal segment based on path length. Don't know whether this is strictly
        // necessary. Might be a bit expensive. Maybe drop it and use fixed number of segments.
        var firstStats = first.getStats(INTERPOLATION_POINTS)
        val ip=min(22,max(5,firstStats.length.toInt()/5))
        firstStats = first.getStats(ip)
        val secondStats = second.getStats(ip)

        // absolute position error based on RMSE with dead band. Deviations within the dead band
        // contribute nothing to the sum. Deviations larger than dead band are squared. Dead band
        // is KanjiSize/n. Larger values for n are harder, lower values are easier.
        val pointsDistanceSum = firstStats.evenlyApproximated.zip(secondStats.evenlyApproximated)
            .sumOf {max(0f,euclDistance(it.first, it.second)- KanjiSize/22f).toDouble().pow(2.0) }

        // normalize then take square root
        val pointsDistanceError = sqrt(pointsDistanceSum/ip)

        var directionError=0.0

        // for all path segments of the first path, compute the angle between it and the x-axis
        for(i in 0 until ip-1) {
            val firstDirection = atan2(
                (firstStats.evenlyApproximated[i+1].y - firstStats.evenlyApproximated[i].y).toDouble(),
                (firstStats.evenlyApproximated[i+1].x - firstStats.evenlyApproximated[i].x).toDouble()
            )

            // since atan2 wraps around at 180 degrees, we avoid nasty conditional blocks by rotating
            // the corresponding segment of the second path by the angle computed for the first path
            // in order to keep the differences small and avoid any edge cases. That is, the angle
            // computed for the rotated line segment is exactly the deviation to the first line but
            // within the interval [-PI,PI] which is the interval atan2 returns.
            val tx=(secondStats.evenlyApproximated[i+1].x-secondStats.evenlyApproximated[i].x)*cos(-firstDirection)-
                    (secondStats.evenlyApproximated[i+1].y-secondStats.evenlyApproximated[i].y)*sin(-firstDirection)
            val ty=(secondStats.evenlyApproximated[i+1].y-secondStats.evenlyApproximated[i].y)*cos(-firstDirection)+
                    (secondStats.evenlyApproximated[i+1].x-secondStats.evenlyApproximated[i].x)*sin(-firstDirection)

            // Multiply by a constant and square so that larger deviations are worse.
            directionError+=(5*atan2(ty,tx)).pow(2.0)
        }

        // normalize and take square root.
        directionError=sqrt(directionError/ip)

        val cumulativeError = pointsDistanceError.toFloat() + directionError.toFloat()
        Logger.d("error[$cumulativeError] distanceErr[$pointsDistanceError]")
        println("ALT: error[$cumulativeError] distanceErr[$pointsDistanceError] directionErr[$directionError] ip[$ip]")
        return cumulativeError
    }

    private fun List<PointF>.center(): PointF {
        return PointF(
            sumOf { it.x.toDouble() / size }.toFloat(),
            sumOf { it.y.toDouble() / size }.toFloat()
        )
    }

    private fun List<PointF>.minus(value: PointF): List<PointF> {
        return map { PointF(it.x - value.x, it.y - value.y) }
    }

    private fun relativeScale(
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

    private fun List<PointF>.scaled(scaleX: Float, scaleY: Float): List<PointF> {
        return map { PointF(it.x * scaleX, it.y * scaleY) }
    }

    private fun euclDistance(
        pointA: PointF,
        pointB: PointF,
    ): Float {
        return sqrt(
            (pointA.x - pointB.x).pow(2) + (pointA.y - pointB.y).pow(2)
        )
    }

    private class PathStats(
        val evenlyApproximated: List<PointF>,
        val length: Float
    )

    private fun Path.getStats(ip:Int): PathStats {
        val approximatedPath = approximateEvenly(ip)
        return PathStats(
            length = approximatedPath.length,
            evenlyApproximated = approximatedPath.points.map { PointF(it.x, it.y) }
        )
    }

}
