package ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.theme.KanjiDojoTheme
import ua.syt0r.kanji.presentation.common.theme.secondaryDark
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.CustomTopBar
import ua.syt0r.kanji.presentation.common.ui.kanji.Kanji
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
                upButtonVisible = true,
                onUpButtonClick = onUpClick
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

    Row(
        modifier = Modifier.padding(12.dp)
    ) {

        val buttonStyle = Modifier.weight(1f)
        val buttonShape = RoundedCornerShape(24.dp)

        OutlinedButton(
            onClick = { /*TODO*/ },
            modifier = buttonStyle,
            shape = buttonShape
        ) {

            Text(text = "Repeat Later", color = secondaryDark)

        }

        Spacer(modifier = Modifier.width(12.dp))

        OutlinedButton(
            onClick = { /*TODO*/ },
            modifier = buttonStyle,
            shape = buttonShape
        ) {

            Text(text = "Good", color = Color.Black)

        }

    }

}

@Composable
fun ReviewInProgress(
    state: State.ReviewingKanji
) {

    Column(
        modifier = Modifier.padding(24.dp)
    ) {

        if (state.kun.isNotEmpty())
            KanjiInfoSection(title = "kun readings:", dataList = state.kun)

        if (state.on.isNotEmpty())
            KanjiInfoSection(title = "on readings:", dataList = state.on)

        if (state.meanings.isNotEmpty())
            KanjiInfoSection(title = "meanings:", dataList = state.meanings)

        Row {
            KanjiInput(
                strokes = state.strokes,
                strokesToDraw = state.drawnStrokesCount,
                onStrokeDrawn = { a, b -> }
            )
        }

    }
}

@Composable
fun KanjiInfoSection(
    title: String,
    dataList: List<String>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            modifier = Modifier.weight(1f)
        )

        AutoBreakRow(
            modifier = Modifier.weight(2f)
        ) {

            dataList.forEach {
                Text(
                    text = it,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 4.dp)
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

        val inputShape = RoundedCornerShape(24.dp)

        Box(
            modifier = Modifier
                .size(inputBoxSize)
                .clip(inputShape)
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = inputShape
                ),
        ) {

            Kanji(
                strokes = strokes.take(strokesToDraw),
                modifier = Modifier.fillMaxSize()
            )

            KanjiUserInput(
                modifier = Modifier.fillMaxSize(),
                strokes = strokes,
                strokesToDraw = strokesToDraw
            ) {

                onStrokeDrawn(it, inputBoxSizePx)

            }
        }


    }

}


@Preview(showBackground = true)
@Composable
private fun LoadingStatePreview() {

    KanjiDojoTheme {
        WritingPracticeScreenUI(
            state = State.Init,
            onUpClick = {}
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun LoadedStatePreview() {

    KanjiDojoTheme {
        WritingPracticeScreenUI(
            state = State.ReviewingKanji(
                kanji = "a",
                on = listOf("test"),
                kun = listOf("test"),
                meanings = listOf("tttest"),
                strokes = listOf(),
                drawnStrokesCount = 0
            ),
            onUpClick = {}
        )
    }

}
