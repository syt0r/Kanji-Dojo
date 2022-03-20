package ua.syt0r.kanji.core

import android.graphics.Matrix
import android.graphics.PathMeasure
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import java.lang.Float.min

private const val INTERPOLATION_POINTS = 21

fun Path.readPoints(
    pointsCount: Int = INTERPOLATION_POINTS
): List<FloatArray> {
    val pathMeasure = PathMeasure()
    val positions = FloatArray(2)
    val androidPath = asAndroidPath()
    pathMeasure.setPath(androidPath, false)

    val pathLength = pathMeasure.length

    return (0 until pointsCount + 1).map {
        pathMeasure.getPosTan(
            min(pathLength * it / pointsCount, pathLength),
            positions,
            null
        )
        floatArrayOf(positions[0], positions[1])
    }
}

fun Path.scaled(from: Float, to: Float): Path {
    val matrix = Matrix()
    val scale = to / from
    matrix.setScale(scale, scale)
    return asAndroidPath().run {
        val resultPath = Path()
        transform(matrix, resultPath.asAndroidPath())
        resultPath
    }
}


fun Path.lerpBetween(first: Path, second: Path, lerp: Float) {
    val firstPoints = first.readPoints()
    val secondPoints = second.readPoints()

    val interpolatedCoordinates = firstPoints.zip(secondPoints).map { (point1, point2) ->
        point1.zip(point2).map { (val1, val2) -> val1 + (val2 - val1) * lerp }
    }

    reset()
    val start = interpolatedCoordinates.first()
    moveTo(start[0], start[1])

    interpolatedCoordinates.slice(1 until interpolatedCoordinates.size)
        .forEach { lineTo(it[0], it[1]) }
}
