package ua.syt0r.kanji.core.stroke_evaluator

import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji.core.PathApproximation
import ua.syt0r.kanji.core.PointF
import ua.syt0r.kanji.core.approximateEquidistant
import ua.syt0r.kanji.core.euclDistance
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.common.ui.kanji.KanjiSize
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class AltKanjiStrokeEvaluator : KanjiStrokeEvaluator {

    companion object {
        // difficulty. lower is easier.
        private const val DIFFICULTY = 1.0f

        private const val SIMILARITY_ERROR_THRESHOLD = 10f

        // divide path into segments of this length
        private const val SEGMENT_LENGTH = 5f

        // dead band below which there is no error contribution
        private const val POSITIONAL_DEAD_BAND = 44f / DIFFICULTY

        // dead band (in degrees) below which there is no error contribution
        private const val DIRECTIONAL_DEAD_BAND = 5f / DIFFICULTY

        // skipping path segments of either path to get a better match still incurs some error
        // see function gapCost()
        private const val GAP_OPENING = 5f
        private const val GAP_EXTENSION = 5f

        // max positional error in number of dead bands
        private const val MAX_POSITIONAL_ERROR = 6f / DIFFICULTY

        // max directional error in degrees
        private const val MAX_DIRECTIONAL_ERROR = 30f / DIFFICULTY

        // scaling constant to make balancing with gap costs easier
        private const val ERROR_SCALE = 20f
    }

    private data class Vector2d(val a: Double, val b: Double) : Comparable<Vector2d> {
        operator fun plus(other: Vector2d) = Vector2d(a + other.a, b + other.b)

        // adding equal amounts of scalar to both components
        operator fun plus(scalar: Double): Vector2d {
            if ((a == 0.0) && (b == 0.0)) {
                val ab = sqrt(0.5 * (scalar.pow(2.0)))
                return Vector2d(ab, ab)
            }

            if (a == 0.0) return Vector2d(0.0, b + scalar)

            // the following handles all other cases including b==0.0
            val newLength = this.length() + scalar
            val denominator = sqrt((b / a).pow(2.0) + 1)

            return Vector2d(newLength / denominator, newLength * (b / a) / denominator)
        }

        operator fun div(scalar: Double): Vector2d = Vector2d(this.a / scalar, this.b / scalar)

        // there's no order on complex numbers or 2D vectors. We do it a bit wonky here.
        // correct? no! sufficient? yes!
        override operator fun compareTo(other: Vector2d): Int {
            // both components strictly larger -> this larger than other
            if ((a > other.a) && (b > other.b)) return 1

            // both components strictly smaller -> this smaller than other
            if ((a < other.a) && (b < other.b)) return -1

            // here be dragons
            return if (this.length() > other.length()) 1
            else -1
        }

        fun length(): Double = sqrt(a.pow(2.0) + b.pow(2.0))
    }

    override fun areStrokesSimilar(
        first: Path, second: Path
    ): Boolean {
        val error = getError(first, second)
        return error <= SIMILARITY_ERROR_THRESHOLD
    }

    private fun gapCost(length: Int): Double =
        GAP_OPENING + GAP_EXTENSION * (length.toDouble().pow(2.0))

    private fun <T> minWhence(values: List<Vector2d>, offsets: List<T>): Pair<Vector2d, T> {
        assert(values.size == offsets.size)
        var minimum = values[0]
        var offset = offsets[0]

        (1 until values.size).forEach { position ->
            if (values[position] < minimum) {
                minimum = values[position]
                offset = offsets[position]
            }
        }

        return Pair(minimum, offset)
    }

    private fun getError(first: Path, second: Path): Float {
        val firstApproximation = first
            .approximateEquidistant(SEGMENT_LENGTH) as? PathApproximation.Success
            ?: return SIMILARITY_ERROR_THRESHOLD + 1

        val secondApproximation = second
            .approximateEquidistant(SEGMENT_LENGTH) as? PathApproximation.Success
            ?: return SIMILARITY_ERROR_THRESHOLD + 1

        val distanceMatrix: Array<Array<Vector2d>> =
            Array(firstApproximation.points.size) {
                Array(secondApproximation.points.size) { Vector2d(0.0, 0.0) }
            }

        for (column in 1 until secondApproximation.points.size) distanceMatrix[0][column] =
            Vector2d(0.0, 0.0) + gapCost(column)

        for (row in 1 until firstApproximation.points.size) distanceMatrix[row][0] =
            Vector2d(0.0, 0.0) + gapCost(row)

        for (row in 1 until firstApproximation.points.size) {
            for (column in 1 until secondApproximation.points.size) {
                var rowMin = distanceMatrix[row][0]
                var rowMinAt = 0
                var columnMin = distanceMatrix[0][column]
                var columnMinAt = 0

                (1 until column).forEach { k ->
                    if (distanceMatrix[row][k] < rowMin) {
                        rowMin = distanceMatrix[row][k]
                        rowMinAt = k
                    }
                }

                (1 until row).forEach { k ->
                    if (distanceMatrix[k][column] < columnMin) {
                        columnMin = distanceMatrix[k][column]
                        columnMinAt = k
                    }
                }

                rowMin += gapCost(column - rowMinAt)
                columnMin += gapCost(row - columnMinAt)

                distanceMatrix[row][column] = minOf(
                    rowMin, columnMin, distanceMatrix[row - 1][column - 1] + Vector2d(
                        (distanceError(
                            firstApproximation.points[row - 1],
                            secondApproximation.points[column - 1]
                        ) + distanceError(
                            firstApproximation.points[row],
                            secondApproximation.points[column]
                        )) / 2.0, directionalError(
                            firstApproximation.points[row - 1],
                            firstApproximation.points[row],
                            secondApproximation.points[column - 1],
                            secondApproximation.points[column]
                        )
                    )
                )
            }
        }

        var row = firstApproximation.points.size - 1
        var column = secondApproximation.points.size - 1
        var pathLength = 0

        while ((row > 0) && (column > 0)) {
            val offset = minWhence(
                listOf(
                    distanceMatrix[row - 1][column - 1],
                    distanceMatrix[row][column - 1],
                    distanceMatrix[row - 1][column]
                ), listOf(Pair(-1, -1), Pair(0, -1), Pair(-1, 0))
            ).second

            row += offset.first
            column += offset.second
            pathLength++
        }

        pathLength += row + column
        if (firstApproximation.points.size < 10) pathLength++
        if (firstApproximation.points.size < 5) pathLength++

        val error =
            distanceMatrix[firstApproximation.points.size - 1][secondApproximation.points.size - 1] / pathLength.toDouble()
        Logger.d("error[${error.length()}] distanceErr[${error.a}] directionErr[${error.b}]")
        return error.length().toFloat()
    }

    private fun directionalError(
        pointA1: PointF, pointA2: PointF, pointB1: PointF, pointB2: PointF
    ): Double {
        val firstDirection = atan2(
            (pointA2.y - pointA1.y).toDouble(), (pointA2.x - pointA1.x).toDouble()
        )

        // since atan2 wraps around at 180 degrees, we avoid nasty conditional blocks by rotating
        // the corresponding segment of the second path by the angle computed for the first path
        // in order to keep the differences small and avoid any edge cases. That is, the angle
        // computed for the rotated line segment is exactly the deviation to the first line but
        // within the interval [-PI,PI] which is the interval atan2 returns.
        val tx =
            (pointB2.x - pointB1.x) * cos(-firstDirection) - (pointB2.y - pointB1.y) * sin(-firstDirection)
        val ty =
            (pointB2.y - pointB1.y) * cos(-firstDirection) + (pointB2.x - pointB1.x) * sin(-firstDirection)

        // Normalize, cap and multiply by a constant
        return ERROR_SCALE * min(
            1.0,
            max(
                0.0,
                abs(180f * atan2(ty, tx) / Math.PI) - DIRECTIONAL_DEAD_BAND
            ) / MAX_DIRECTIONAL_ERROR
        )
    }

    private fun distanceError(pointA: PointF, pointB: PointF): Double {
        val error = (max(
            0f, euclDistance(pointA, pointB) - KanjiSize / POSITIONAL_DEAD_BAND
        ) / (MAX_POSITIONAL_ERROR * KanjiSize / POSITIONAL_DEAD_BAND)).toDouble().pow(2.5)

        // cap and scale
        return ERROR_SCALE * min(1.0, error)
    }
}
