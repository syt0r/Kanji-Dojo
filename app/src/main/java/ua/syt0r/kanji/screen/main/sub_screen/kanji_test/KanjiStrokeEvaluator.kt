package ua.syt0r.kanji.screen.main.sub_screen.kanji_test

import android.graphics.PathMeasure
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import com.google.gson.Gson
import ua.syt0r.kanji.core.logger.Logger
import kotlin.math.abs

class KanjiStrokeEvaluator {

    companion object {
        private const val SIMILARITY_ERROR_THRESHOLD = 80f
    }

    fun areSimilar(
        predefinedPath: Path,
        userPath: Path,
        userDrawAreaSize: Int
    ): Boolean {
        val predefinedPoints = readPoints(predefinedPath)
            .also { it.print { "predefinedPoints" } }

        val userPoints = readPoints(userPath)
            .also { it.print { "userPoints" } }
            .scaled(userDrawAreaSize, 109)
            .also { it.print { "userPointsScaledToKanjiSize" } }

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

        // TODO might be useful to calculate error, not used 
        val centerDifference = predefinedPointsCenter.zip(userPointsCenter)
            .map { (x1, x2) -> abs(x1 - x2) }
        Logger.d("centerDifference[$centerDifference]")

        val centeredPredefinedPoints = predefinedPoints.minus(predefinedPointsCenter)
            .also { it.print { "centeredPredefinedPoints" } }
        val centeredUserPoints = userPoints.minus(userPointsCenter)
            .also { it.print { "centeredUserPoints" } }

        val relativeScale = relativeScale(predefinedPoints, userPoints)
        Logger.d("relativeScale[${relativeScale.toList()}]")

        val scaledUserPoints = centeredUserPoints.scaled(relativeScale)
            .also { it.print { "scaledUserPoints" } }

        val pointsDistance = pointsDistance(centeredPredefinedPoints, scaledUserPoints)

        Logger.d("pointsDistance[$pointsDistance]")

        return pointsDistance
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


    private fun List<FloatArray>.print(message: () -> String = { "" }) {
        Logger.d("data[${message()}]=${Gson().toJson(this)}")
    }

}