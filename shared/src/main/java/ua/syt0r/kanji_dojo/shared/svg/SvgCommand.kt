package ua.syt0r.kanji_dojo.shared.svg

data class Point(
    val x: Float,
    val y: Float
)

sealed class SvgCommand {

    abstract val isAbsoluteCoordinates: Boolean

    data class MoveTo(
        override val isAbsoluteCoordinates: Boolean,
        val point: Point
    ) : SvgCommand() {

        companion object {
            const val pointsRequired = 1
        }

    }

    data class CubicBezierCurve(
        override val isAbsoluteCoordinates: Boolean,
        val point1: Point,
        val point2: Point,
        val point3: Point
    ) : SvgCommand() {

        companion object {
            const val pointsRequired = 3
        }

    }

    data class QuadraticBezierCurve(
        override val isAbsoluteCoordinates: Boolean,
        val point1: Point,
        val point2: Point
    ) : SvgCommand() {

        companion object {
            const val pointsRequired = 2
        }

    }

}