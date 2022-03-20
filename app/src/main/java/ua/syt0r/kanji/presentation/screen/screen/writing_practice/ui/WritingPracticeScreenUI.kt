package ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.lerpBetween
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.kanji.Kanji
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.common.ui.kanji.Stroke
import ua.syt0r.kanji.presentation.common.ui.kanji.StrokeInput
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawResult
import java.lang.Integer.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingPracticeScreenUI(
    state: State,
    onUpClick: () -> Unit,
    submitUserInput: (DrawData) -> Flow<DrawResult>,
    onAnimationCompleted: (DrawResult) -> Unit
) {

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = "Practice") },
                navigationIcon = {
                    IconButton(onClick = onUpClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_info_24),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {

        when (state) {
            State.Init, State.Loading -> {}
            is State.ReviewingKanji -> ReviewInProgress(
                state,
                submitUserInput,
                onAnimationCompleted
            )
            is State.Summary -> {}
        }

    }

}

@Composable
private fun ReviewInProgress(
    state: State.ReviewingKanji,
    onStrokeDrawn: (DrawData) -> Flow<DrawResult>,
    onAnimationCompleted: (DrawResult) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = state.meanings.first().capitalize(Locale.current),
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (state.kun.isNotEmpty())
            KanjiInfoSection(dataList = state.kun)

        if (state.on.isNotEmpty())
            KanjiInfoSection(dataList = state.on)

        Spacer(modifier = Modifier.weight(1f))

        Box {

            KanjiInput(
                strokes = state.strokes,
                strokesToDraw = state.drawnStrokesCount,
                onStrokeDrawn = onStrokeDrawn,
                onAnimationCompleted = onAnimationCompleted
            )

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp),
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_navigate_next_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )

            }

        }

    }
}

@Composable
private fun KanjiInfoSection(dataList: List<String>) {

    Row {

        AutoBreakRow(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.Start
        ) {

            dataList.forEach {

                Text(
                    text = it,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    maxLines = 1
                )

            }

        }
    }

}

@Composable
private fun KanjiInput(
    strokes: List<Path>,
    strokesToDraw: Int,
    onStrokeDrawn: (DrawData) -> Flow<DrawResult>,
    onAnimationCompleted: (DrawResult) -> Unit
) {

    val inputBoxSize = LocalConfiguration.current.screenWidthDp.dp.minus(40.dp)
    val inputShape = RoundedCornerShape(20.dp)

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .size(inputBoxSize)
            .clip(inputShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = inputShape
            ),
    ) {

        val animatedProgress = remember { Animatable(initialValue = 0f) }

        val animationData: MutableState<DrawResult?> = remember {
            mutableStateOf(null, neverEqualPolicy())
        }

        if (animatedProgress.value == 1f && animationData.value != null) {
            onAnimationCompleted(animationData.value!!)
            animationData.value = null
        }

        Kanji(
            strokes = strokes.take(
                if (animationData.value.let { it == null || it !is DrawResult.Correct }) strokesToDraw
                else max(strokesToDraw - 1, 0) // Last stroke is handled by animation
            ),
            modifier = Modifier.fillMaxSize()
        )

        when (val drawResult = animationData.value) {
            is DrawResult.Correct -> {
                val path = Path()
                path.lerpBetween(
                    drawResult.userDrawnPath,
                    drawResult.kanjiPath,
                    animatedProgress.value
                )
                Stroke(
                    path = path,
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                )
            }
            is DrawResult.Mistake -> {
                Stroke(
                    path = drawResult.path,
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Red.copy(alpha = 1 - animatedProgress.value)
                )
            }
            null -> {}
        }

        StrokeInput(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(1) {
                    detectDragGestures(
                        onDragStart = {
                            coroutineScope.launch { animatedProgress.snapTo(0f) }
                            animationData.value?.let { onAnimationCompleted(it) }
                            animationData.value = null
                        },
                        onDrag = { _, _ -> }
                    )
                },
        ) { drawnPath ->

            onStrokeDrawn.invoke(DrawData(drawnPath = drawnPath))
                .onEach {
                    animationData.value = it
                    animatedProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(1000)
                    )
                }
                .launchIn(coroutineScope)

        }
    }

}

@Preview(showBackground = true)
@Composable
private fun LoadingStatePreview() {

    AppTheme {
        WritingPracticeScreenUI(
            state = State.Init,
            onUpClick = {},
            submitUserInput = { flow { } },
            onAnimationCompleted = {}
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun LoadedStatePreview() {

    AppTheme {
        WritingPracticeScreenUI(
            state = PreviewKanji.run {
                State.ReviewingKanji(
                    kanji = kanji,
                    on = on,
                    kun = kun,
                    meanings = meanings,
                    strokes = strokes,
                    drawnStrokesCount = 3
                )
            },
            onUpClick = {},
            submitUserInput = { flow { } },
            onAnimationCompleted = {}
        )
    }

}
