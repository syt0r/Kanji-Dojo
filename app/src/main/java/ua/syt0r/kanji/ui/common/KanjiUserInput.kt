package ua.syt0r.kanji.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.rawDragGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import ua.syt0r.kanji.core.logger.Logger

@Composable
fun KanjiUserInput(
    modifier: Modifier = Modifier,
    strokes: List<Path>,
    strokesToDraw: Int,
    onUserPathDrawn: (Path) -> Unit
) {

    Box(
        modifier = modifier
    ) {

        Kanji(
            strokes = strokes,
            strokesToDraw = strokesToDraw,
            modifier = Modifier.matchParentSize()
        )

        val drawPathState = mutableStateOf(Path(), neverEqualPolicy())

        val dragObserver = object : DragObserver {
            override fun onStart(downPosition: Offset) {
                Logger.d("onStart downPosition[$downPosition]")
                drawPathState.value = Path().apply { moveTo(downPosition.x, downPosition.y) }
            }

            override fun onDrag(dragDistance: Offset): Offset {
                Logger.d("onDrag dragDistance[$dragDistance]")
                drawPathState.value = drawPathState.value.apply {
                    relativeLineTo(dragDistance.x, dragDistance.y)
                }
                return dragDistance
            }

            override fun onStop(velocity: Offset) {
                onUserPathDrawn(drawPathState.value)
                drawPathState.value = Path()
            }
        }

        Canvas(
            modifier = Modifier
                .matchParentSize()
                .rawDragGestureFilter(dragObserver)
        ) {

            clipRect {
                drawPath(
                    path = drawPathState.value,
                    color = Color.Red,
                    style = Stroke(
                        width = 3f / 109 * drawContext.size.width,
                        cap = StrokeCap.Round
                    )
                )
            }

        }

    }

}