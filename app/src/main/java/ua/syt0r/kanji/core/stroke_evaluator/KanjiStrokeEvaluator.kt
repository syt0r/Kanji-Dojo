package ua.syt0r.kanji.core.stroke_evaluator

import android.graphics.PointF
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.core.graphics.minus
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.common.ui.kanji.KanjiSize
import javax.inject.Inject
import kotlin.math.*

class KanjiStrokeEvaluator @Inject constructor() {

    companion object {
        private const val SIMILARITY_ERROR_THRESHOLD = 100f
    }

    fun areStrokesSimilar(
        first: Path,
        second: Path
    ): Boolean {
        val error = getError(first, second)
        return error <= SIMILARITY_ERROR_THRESHOLD
    }

    private fun getError(first: Path, second: Path): Float {
        val firstStats = first.getStats()
        val secondStats = second.getStats()

        val lengthDiff = abs(firstStats.length - secondStats.length)
        val lengthDifferenceError = 20f * lengthDiff / KanjiSize

        val firstCenter = firstStats.evenlyApproximated.center()
        val secondCenter = secondStats.evenlyApproximated.center()

        val centerDifferenceError = 2f * euclDistance(firstCenter, secondCenter)

        val centeredFirstPoints = firstStats.evenlyApproximated.minus(firstCenter)
        val centeredSecondPoints = secondStats.evenlyApproximated.minus(secondCenter)

        val (firstScale, secondScale) = relativeScale(centeredFirstPoints, centeredSecondPoints)

        val widthScaleDiff = max(firstScale.width, secondScale.width) /
                min(firstScale.width, secondScale.width)
        val heightScaleDiff = max(firstScale.height, secondScale.height) /
                min(firstScale.height, secondScale.height)

        val relativeScaleError = 5f * (widthScaleDiff + heightScaleDiff)

        val firstScaledToSecond = centeredFirstPoints.scaled(
            scaleX = secondScale.width / firstScale.width,
            scaleY = secondScale.height / firstScale.height
        )

        val pointsDistanceSum = firstScaledToSecond.zip(centeredSecondPoints)
            .sumOf { euclDistance(it.first, it.second).toDouble() }.toFloat()
        val pointsDistanceError = .2f * pointsDistanceSum

        val cumulativeError = lengthDifferenceError + centerDifferenceError +
                relativeScaleError + pointsDistanceError
        Logger.d("error[$cumulativeError] lengthErr[$lengthDifferenceError] centerDiffErr[$centerDifferenceError] scaleErr[$relativeScaleError] distanceErr[$pointsDistanceError]")
        return cumulativeError
    }

    private fun List<PointF>.center(): PointF {
        return PointF(
            sumOf { it.x.toDouble() / size }.toFloat(),
            sumOf { it.y.toDouble() / size }.toFloat()
        )
    }

    private fun List<PointF>.minus(value: PointF): List<PointF> {
        return map { PointF(it.x, it.y).minus(value) }
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


}