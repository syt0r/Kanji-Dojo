package ua.syt0r.kanji.core.stroke_evaluator

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji.core.PathApproximation
import ua.syt0r.kanji.core.approximateEvenly
import ua.syt0r.kanji.core.center
import ua.syt0r.kanji.core.decreaseAll
import ua.syt0r.kanji.core.euclDistance
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.relativeScale
import ua.syt0r.kanji.core.scaled
import ua.syt0r.kanji.presentation.common.ui.kanji.KanjiSize
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class DefaultKanjiStrokeEvaluator : KanjiStrokeEvaluator {

    companion object {
        private const val SIMILARITY_ERROR_THRESHOLD = 100f
        private const val INTERPOLATION_POINTS = 22
        private const val MIN_SCALE_DIMENSION = 1f
    }

    override fun areStrokesSimilar(
        first: Path, second: Path
    ): Boolean {
        val error = getError(first, second)
        return error <= SIMILARITY_ERROR_THRESHOLD
    }

    private fun getError(first: Path, second: Path): Float {
        val firstApproximation = first
            .approximateEvenly(INTERPOLATION_POINTS) as? PathApproximation.Success

        if (firstApproximation == null) {
            Logger.d("Couldn't approximate first path")
            return SIMILARITY_ERROR_THRESHOLD + 1
        }

        val secondApproximation = second
            .approximateEvenly(INTERPOLATION_POINTS) as? PathApproximation.Success

        if (secondApproximation == null) {
            Logger.d("Couldn't approximate second path")
            return SIMILARITY_ERROR_THRESHOLD + 1
        }

        val lengthDiff = abs(firstApproximation.length - secondApproximation.length)
        val lengthDifferenceError = 20f * lengthDiff / KanjiSize

        val firstCenter = firstApproximation.points.center()
        val secondCenter = secondApproximation.points.center()

        val centerDifferenceError = 2f * euclDistance(firstCenter, secondCenter)

        val centeredFirstPoints = firstApproximation.points.decreaseAll(firstCenter)
        val centeredSecondPoints = secondApproximation.points.decreaseAll(secondCenter)

        val (firstScale, secondScale) = relativeScale(centeredFirstPoints, centeredSecondPoints)
        val relativeScaleError = getScaleError(firstScale, secondScale)

        val firstScaledToSecond = centeredFirstPoints.scaled(
            scaleX = secondScale.width / firstScale.width,
            scaleY = secondScale.height / firstScale.height
        )

        val pointsDistanceSum = firstScaledToSecond.zip(centeredSecondPoints)
            .sumOf { euclDistance(it.first, it.second).toDouble() }.toFloat()
        val pointsDistanceError = .2f * pointsDistanceSum

        val cumulativeError =
            lengthDifferenceError + centerDifferenceError + relativeScaleError + pointsDistanceError
        Logger.d("error[$cumulativeError] lengthErr[$lengthDifferenceError] centerDiffErr[$centerDifferenceError] scaleErr[$relativeScaleError] distanceErr[$pointsDistanceError]")
        return cumulativeError
    }

    private fun getScaleError(firstScale: Size, secondScale: Size): Float {
        // Limiting min scale to avoid big scale difference when kanji has straight lines
        val safe1Scale = firstScale.withMinSide(MIN_SCALE_DIMENSION)
        val safe2Scale = secondScale.withMinSide(MIN_SCALE_DIMENSION)

        val widthScaleDiff = bigSideToShortSideRatio(safe1Scale.width, safe2Scale.width)
        val heightScaleDiff = bigSideToShortSideRatio(safe1Scale.height, safe2Scale.height)

        return 5f * (widthScaleDiff + heightScaleDiff)
    }

    private fun Size.withMinSide(minSideValue: Float) = Size(
        max(minSideValue, width),
        max(minSideValue, height)
    )

    private fun bigSideToShortSideRatio(side1: Float, side2: Float): Float {
        return max(side1, side2) / min(side1, side2)
    }

}