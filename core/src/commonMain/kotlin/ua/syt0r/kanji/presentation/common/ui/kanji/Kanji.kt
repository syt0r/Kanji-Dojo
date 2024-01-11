package ua.syt0r.kanji.presentation.common.ui.kanji

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.svg.SvgCommandParser
import ua.syt0r.kanji.core.lerpTo
import ua.syt0r.kanji.core.svg.SvgPathCreator
import ua.syt0r.kanji.presentation.common.ExcludeNavigationGesturesModifier

const val KanjiSize = 109
const val StrokeWidth = 3f

@Composable
fun defaultStrokeColor(): Color {
    return MaterialTheme.colorScheme.onSurface
}

@Composable
fun Kanji(
    strokes: List<Path>,
    modifier: Modifier = Modifier,
    strokeColor: Color = defaultStrokeColor(),
    stokeWidth: Float = StrokeWidth
) {
    Canvas(modifier) {
        strokes.forEach { drawKanjiStroke(it, strokeColor, stokeWidth) }
    }
}

@Composable
fun Kanji(
    strokes: State<List<Path>>,
    modifier: Modifier = Modifier,
    strokeColor: Color = defaultStrokeColor(),
    stokeWidth: Float = StrokeWidth
) {
    Canvas(modifier) {
        strokes.value.forEach { drawKanjiStroke(it, strokeColor, stokeWidth) }
    }
}

@Composable
fun Stroke(
    path: Path,
    modifier: Modifier = Modifier,
    color: Color = defaultStrokeColor(),
    stokeWidth: Float = StrokeWidth
) {
    Canvas(modifier) {
        clipRect { drawKanjiStroke(path, color, stokeWidth) }
    }
}

@Composable
fun StrokeInput(
    onUserPathDrawn: suspend (Path) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = defaultStrokeColor(),
    stokeWidth: Float = StrokeWidth
) {

    val coroutineScope = rememberCoroutineScope()

    val drawPathState = remember { mutableStateOf(Path(), neverEqualPolicy()) }
    var areaSize by remember { mutableStateOf(0) }

    Canvas(
        modifier = modifier
            .then(ExcludeNavigationGesturesModifier)
            .onGloballyPositioned { areaSize = it.size.height }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        drawPathState.value = Path().apply {
                            moveTo(
                                it.x / areaSize * KanjiSize,
                                it.y / areaSize * KanjiSize
                            )
                        }
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            onUserPathDrawn(drawPathState.value)
                            drawPathState.value = Path()
                        }
                    },
                    onDrag = { _, dragAmount ->
                        drawPathState.value = drawPathState.value.apply {
                            relativeLineTo(
                                dragAmount.x / areaSize * KanjiSize,
                                dragAmount.y / areaSize * KanjiSize
                            )
                        }
                    }
                )
            }
    ) {
        clipRect {
            val path = drawPathState.value
            drawKanjiStroke(path, color, stokeWidth)
        }
    }

}

@Composable
fun AnimatedStroke(
    fromPath: Path,
    toPath: Path,
    progress: () -> Float,
    modifier: Modifier = Modifier,
    strokeColor: Color = defaultStrokeColor(),
    stokeWidth: Float = StrokeWidth
) {
    Canvas(modifier) {
        val path = fromPath.lerpTo(toPath, progress())
        drawKanjiStroke(path, strokeColor, stokeWidth)
    }
}

expect fun DrawScope.drawKanjiStroke(
    path: Path,
    color: Color,
    width: Float,
    drawProgress: Float? = null
)

fun parseKanjiStrokes(strokes: List<String>): List<Path> {
    return strokes.map { SvgCommandParser.parse(it) }
        .map { SvgPathCreator.convert(it) }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun KanjiPreview() {
//    AppTheme {
//        Column {
//            Kanji(
//                modifier = Modifier
//                    .size(200.dp)
//                    .background(MaterialTheme.colorScheme.background),
//                strokes = PreviewKanji.strokes
//            )
//        }
//
//    }
//}
