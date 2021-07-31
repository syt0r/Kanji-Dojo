package ua.syt0r.kanji.core.svg

import androidx.compose.ui.graphics.Path
import ua.syt0r.svg.SvgCommand

object SvgPathCreator {

    fun convert(commands: List<SvgCommand>): Path {
        val path = Path()
        commands.forEach { path.applyCommand(it) }
        return path
    }

    private fun Path.applyCommand(command: SvgCommand) = when (command) {
        is SvgCommand.MoveTo -> {
            if (command.isAbsoluteCoordinates) {
                moveTo(command.point.x, command.point.y)
            } else {
                relativeMoveTo(command.point.x, command.point.y)
            }
        }
        is SvgCommand.CubicBezierCurve -> {
            if (command.isAbsoluteCoordinates) {
                cubicTo(
                    command.point1.x,
                    command.point1.y,
                    command.point2.x,
                    command.point2.y,
                    command.point3.x,
                    command.point3.y
                )
            } else {
                relativeCubicTo(
                    command.point1.x,
                    command.point1.y,
                    command.point2.x,
                    command.point2.y,
                    command.point3.x,
                    command.point3.y
                )
            }
        }
        is SvgCommand.QuadraticBezierCurve -> {
            if (command.isAbsoluteCoordinates) {
                quadraticBezierTo(
                    command.point1.x,
                    command.point1.y,
                    command.point2.x,
                    command.point2.y
                )
            } else {
                relativeQuadraticBezierTo(
                    command.point1.x,
                    command.point1.y,
                    command.point2.x,
                    command.point2.y,
                )
            }
        }
    }

}