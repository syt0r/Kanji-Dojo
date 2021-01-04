package ua.syt0r.kanji.core.curve_evaluator

import android.graphics.PathMeasure
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import ua.syt0r.kanji.core.logger.Logger
import kotlin.math.abs

class KanjiStrokeEvaluator : CurveEvaluatorContract.CurveEvaluator {

    companion object {
        private const val SIMILARITY_ERROR_THRESHOLD = 80f
    }

    override fun areCurvesSimilar(
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
        userPath: Path,
        userDrawAreaSize: Int
    ): Boolean {

        val predefinedPoints = readPoints(predefinedPath)
        val userPoints = readPoints(userPath).scaled(userDrawAreaSize, 109)

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
        Logger.d("pointsDistanceError[$pointsDistanceError]")

        val cumulativeError = centerDifferenceError + relativeScaleError + pointsDistanceError
        Logger.d("cumulativeError[$cumulativeError]")

        return cumulativeError
    }

    private fun readPoints(path: Path): List<FloatArray> {
        val pathMeasure = PathMeasure()
        val positions = FloatArray(2)
        val currentPath = path.asAndroidPath()
        pathMeasure.setPath(currentPath, false)

        val points = mutableListOf<FloatArray>()

        val stepPercent = 0.06f
        var t = 0f
        val pathLength = pathMeasure.length

        while (t <= pathLength) {
            pathMeasure.getPosTan(t, positions, null)
            points.add(
                floatArrayOf(positions[0], positions[1])
            )
            t += pathLength * stepPercent
        }
        return points
    }

    private fun List<FloatArray>.scaled(oldMax: Int, newMax: Int): List<FloatArray> {
        return map {
            it.map { value: Float -> value / oldMax * newMax }.toFloatArray()
        }
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