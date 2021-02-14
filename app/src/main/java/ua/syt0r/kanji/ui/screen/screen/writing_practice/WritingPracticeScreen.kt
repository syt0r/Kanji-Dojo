package ua.syt0r.kanji.ui.screen.screen.writing_practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import org.koin.androidx.compose.getViewModel
import ua.syt0r.kanji.ui.common.KanjiUserInput
import ua.syt0r.kanji.ui.theme.KanjiDojoTheme
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Composable
fun ReviewScreenPreview() {

    val kanjiData = ReviewKanjiData(
        kanji = "太",
        onYomiReadings = listOf("た。いる"),
        kunYomiReading = listOf("ゴン", "ゲン"),
        meaningVariants = listOf("meaningless", "brbr")
    )

    KanjiDojoTheme {
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
                        mainAxisSize = SizeMode.Expand,
                        mainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly
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

            Row {
                KanjiReviewInput(kanji = kanjiData.kanji)
            }


        }

    }

}

@Composable
fun ReviewScreenBottomBar() {

    Row {

        Text(
            text = "Repeat Later",
            modifier = Modifier
                .weight(1f)
                .background(Color.Red)
                .clickable {}
                .padding(vertical = 12.dp)
        )

        Text(
            text = "Accept",
            modifier = Modifier
                .weight(1f)
                .background(Color.Green)
                .clickable {}
                .padding(vertical = 12.dp)
        )

    }

}

@Composable
fun KanjiReviewInput(
    viewModel: WritingPracticeScreenContract.ViewModel = getViewModel<WritingPracticeViewModel>(),
    kanji: String
) {

    viewModel.init(kanji)

    val state by viewModel.state.observeAsState()

    if (state !is WritingPracticeScreenContract.State.DrawingKanji) return

    val (strokes, drawnStrokesCount) = (state as? WritingPracticeScreenContract.State.DrawingKanji)
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
    viewModel: WritingPracticeScreenContract.ViewModel = viewModel(WritingPracticeViewModel::class.java),
    strokes: List<Path>,
    strokesToDraw: Int
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val inputBoxSize = 200.dp
        val inputBoxSizePx = with(AmbientDensity.current) { inputBoxSize.toPx().roundToInt() }

        KanjiUserInput(
            modifier = Modifier
                .size(inputBoxSize)
                .background(Color.White),
            strokes = strokes,
            strokesToDraw = strokesToDraw
        ) {
            viewModel.submitUserDrawnPath(it, inputBoxSizePx)
        }

    }

}