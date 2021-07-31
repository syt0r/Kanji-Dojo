package ua.syt0r.kanji.presentation.common.ui.kanji

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput

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

        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(1, 2) {
                    detectDragGestures(
                        onDragStart = {
                            drawPathState.value = Path().apply { moveTo(it.x, it.y) }
                        },
                        onDragEnd = {
                            onUserPathDrawn(drawPathState.value)
                            drawPathState.value = Path()
                        },
                        onDrag = { change, dragAmount ->
                            drawPathState.value = drawPathState.value.apply {
                                relativeLineTo(dragAmount.x, dragAmount.y)
                            }
                        }
                    )
                }
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