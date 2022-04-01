package ua.syt0r.kanji.core.stroke_evaluator

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.readPoints
import javax.inject.Inject
import kotlin.math.abs

class KanjiStrokeEvaluator @Inject constructor() : StrokeEvaluatorContract.Evaluator {

    companion object {
        private const val SIMILARITY_ERROR_THRESHOLD = 80f
    }

    override fun areStrokesSimilar(
        predefinedData: List<FloatArray>,
        userInputData: List<FloatArray>
    ): Boolean {

        assert(predefinedData.size == userInputData.size) {
            "Size should be the same, but sizes are [${predefinedData.size},${userInputData.size}]"
        }

        val error = getError(predefinedData, userInputData)
        Logger.d("error=[$error]")

        return error <= SIMILARITY_ERROR_THRESHOLD
    }

    fun areSimilar(
        predefinedPath: Path,
        userPath: Path
    ): Boolean {

        val predefinedPoints = predefinedPath.readPoints()
        val userPoints = userPath.readPoints()

        assert(predefinedPoints.size == userPoints.size) {
            "Size should be the same, but sizes are [${predefinedPoints.size},${userPoints.size}]"
        }

        val error = getError(predefinedPoints, userPoints)
        Logger.d("error=[$error]")

        return error <= SIMILARITY_ERROR_THRESHOLD
    }

    private fun getError(predefinedPoints: List<FloatArray>, userPoints: List<FloatArray>): Float {
        val predefinedPointsCenter = predefinedPoints.center()
        val userPointsCenter = userPoints.center()

        val centerDifference = predefinedPointsCenter.zip(userPointsCenter)
            .map { (x1, x2) -> abs(x1 - x2) }

        val centerDifferenceError = 1f * centerDifference.sum()
        Logger.d("centerDifferenceError[$centerDifferenceError]")

        val centeredPredefinedPoints = predefinedPoints.minus(predefinedPointsCenter)
        val centeredUserPoints = userPoints.minus(userPointsCenter)

        val relativeScale = relativeScale(predefinedPoints, userPoints)

        val relativeScaleError = 20f * relativeScale.map { abs(it - 1) }.sum()
        Logger.d("relativeScaleError[$relativeScaleError]")

        val scaledUserPoints = centeredUserPoints.scaled(relativeScale)

        val pointsDistanceError = .5f * pointsDistance(centeredPredefinedPoints, scaledUserPoints)

        val cumulativeError = centerDifferenceError + relativeScaleError + pointsDistanceError

        return cumulativeError
    }

    private fun List<FloatArray>.center(): FloatArray {
        return first().indices
            .map { i ->
                sortedBy { it[i] }.let { it[it.size / 2][i] }
            }
            .toFloatArray()
    }

    private fun List<FloatArray>.minus(value: FloatArray): List<FloatArray> {
        return map {
            it.mapIndexed { i, x -> x - value[i] }.toFloatArray()
        }
    }

    private fun relativeScale(
        firstPath: List<FloatArray>,
        secondPath: List<FloatArray>
    ): FloatArray {

        fun List<FloatArray>.getSize(): Size = Size(
            width = run { maxOf { it[0] } - minOf { it[0] } },
            height = run { maxOf { it[1] } - minOf { it[1] } }
        )

        val firstSize = firstPath.getSize()
        val secondSize = secondPath.getSize()

        return floatArrayOf(
            firstSize.width / secondSize.width,
            firstSize.height / secondSize.height
        )
    }

    private fun List<FloatArray>.scaled(scale: FloatArray): List<FloatArray> {
        return map { it.mapIndexed { i, value -> value * scale[i] }.toFloatArray() }
    }

    private fun pointsDistance(
        firstPath: List<FloatArray>,
        secondPath: List<FloatArray>,
        distanceFun: (Float, Float) -> Float = { x1, x2 -> abs(x1 - x2) }
    ): Float {
        return firstPath.zip(secondPath)
            .map { (pointA, pointB) ->
                pointA.zip(pointB).map { (x1, x2) -> distanceFun.invoke(x1, x2) }.sum()
            }
            .sum()
    }

}