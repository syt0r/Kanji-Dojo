package ua.syt0r.kanji.screen.main.sub_screen.review

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.rawDragGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import org.koin.androidx.compose.getViewModel
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.screen.common.Kanji
import ua.syt0r.kanji.screen.theme.KanjiDrawerTheme
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Composable
fun ReviewScreenPreview() {

    val kanjiData = ReviewKanjiData(
        kanji = "123",
        onYomiReadings = listOf("た。いる"),
        kunYomiReading = listOf("ゴン", "ゲン"),
        meaningVariants = listOf("meaningless", "brbr")
    )

    KanjiDrawerTheme {
        ReviewScreenTest(kanjiData)
    }

}


data class ReviewKanjiData(
    val kanji: String,
    val onYomiReadings: List<String>,
    val kunYomiReading: List<String>,
    val meaningVariants: List<String>
)

@OptIn(ExperimentalLayout::class)
@Composable
fun ReviewScreenTest(kanjiData: ReviewKanjiData) {

    Scaffold(
        bottomBar = { ReviewScreenBottomBar() }
    ) {

        Column(
            modifier = Modifier.padding(24.dp)
        ) {

            Row {

                Text(
                    text = "Kun Reading: ",
                    modifier = Modifier.weight(1f)
                )

                Box(modifier = Modifier.weight(2f)) {
                    FlowRow(
                        mainAxisAlignment = FlowMainAxisAlignment.SpaceAround
                    ) {

                        kanjiData.kunYomiReading.forEach {
                            Text(
                                text = it,
                                color = Color.White,
                                modifier = Modifier
                                    .background(
                                        color = Color.Gray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                    }
                }

            }


        }

    }

}

@Composable
fun ReviewScreenBottomBar() {

    BottomAppBar {

        Text(
            text = "Again",
            modifier = Modifier
                .weight(1f)
                .clickable {}
        )

        Text(
            text = "Good",
            modifier = Modifier
                .weight(1f)
                .clickable {}
        )

    }

}

@Composable
fun ReviewScreen(
    viewModel: KanjiScreenContract.ViewModel = getViewModel<KanjiScreenViewModel>(),
    kanji: String
) {

    viewModel.init(kanji)

    val state by viewModel.state.observeAsState()

    if (state !is KanjiScreenContract.State.DrawingKanji) return

    val (strokes, drawnStrokesCount) = (state as? KanjiScreenContract.State.DrawingKanji)
        ?.run { stokes to drawnStrokesCount }
        ?: return

    KanjiInput(
        viewModel = viewModel,
        strokes = strokes,
        strokesToDraw = drawnStrokesCount
    )
}

@Composable
fun KanjiInput(
    viewModel: KanjiScreenContract.ViewModel = viewModel(KanjiScreenViewModel::class.java),
    strokes: List<Path>,
    strokesToDraw: Int
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val inputBoxSize = 200.dp
        val inputBoxSizePx = with(AmbientDensity.current) { inputBoxSize.toPx().roundToInt() }

        Box(
            modifier = Modifier
                .size(inputBoxSize)
                .background(Color.White)
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
                    viewModel.submitUserDrawnPath(drawPathState.value, inputBoxSizePx)
                    drawPathState.value = Path()
                }
            }

//            UserInputCanvas(
//                modifier = Modifier
//                    .matchParentSize()
//                    .rawDragGestureFilter(dragObserver),
//                path = drawPathState.value
//            )

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

}

//TODO causes out of memory exception, need to investigate
@Composable
fun UserInputCanvas(
    modifier: Modifier = Modifier,
    path: Path
) {
    Canvas(modifier) {

        clipRect {
            drawPath(
                path = path,
                color = Color.Red,
                style = Stroke(
                    width = 3f / 109 * drawContext.size.width,
                    cap = StrokeCap.Round
                )
            )
        }

    }
}