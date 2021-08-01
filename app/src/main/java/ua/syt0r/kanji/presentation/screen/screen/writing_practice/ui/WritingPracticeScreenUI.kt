package ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.theme.KanjiDojoTheme
import ua.syt0r.kanji.presentation.common.ui.CustomTopBar
import ua.syt0r.kanji.presentation.common.ui.kanji.KanjiUserInput
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.State
import kotlin.math.roundToInt

@Composable
fun WritingPracticeScreenUI(
    state: State,
    onUpClick: () -> Unit
) {

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Practice",
                backButtonEnabled = true,
                onBackButtonClick = onUpClick
            )
        },
        bottomBar = {
            ReviewScreenBottomBar()
        }
    ) {

        when (state) {
            State.Init -> {
            }
            is State.ReviewingKanji -> ReviewInProgress(state)
            is State.Summary -> TODO()
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
fun ReviewInProgress(
    state: State.ReviewingKanji
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
                Row {

                    state.kun.forEach {
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
            KanjiInput(
                strokes = state.stokes,
                strokesToDraw = state.drawnStrokesCount,
                onStrokeDrawn = { a, b -> }
            )
        }

    }
}

@Composable
fun KanjiInput(
    strokes: List<Path>,
    strokesToDraw: Int,
    onStrokeDrawn: (Path, Int) -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val inputBoxSize = 200.dp
        val inputBoxSizePx = with(LocalDensity.current) { inputBoxSize.toPx().roundToInt() }

        KanjiUserInput(
            modifier = Modifier
                .size(inputBoxSize)
                .background(Color.White),
            strokes = strokes,
            strokesToDraw = strokesToDraw
        ) {

            onStrokeDrawn(it, inputBoxSizePx)

        }

    }

}


@Preview(showBackground = true)
@Composable
private fun WritingPracticeScreenPreview() {

    KanjiDojoTheme {
        WritingPracticeScreenUI(
            state = State.Init,
            onUpClick = {}
        )
    }

}
