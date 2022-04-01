package ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.lerpBetween
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.common.ui.kanji.Kanji
import ua.syt0r.kanji.presentation.common.ui.kanji.KanjiBackground
import ua.syt0r.kanji.presentation.common.ui.kanji.Stroke
import ua.syt0r.kanji.presentation.common.ui.kanji.StrokeInput
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawResult

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WritingPracticeKanjiInputSection(
    state: WritingPracticeScreenContract.State.ReviewingKanji,
    onStrokeDrawn: (DrawData) -> Flow<DrawResult>,
    onAnimationCompleted: (DrawResult) -> Unit
) {

    val inputBoxSize = LocalConfiguration.current.screenWidthDp.dp.minus(40.dp)
    val inputShape = RoundedCornerShape(20.dp)
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(24.dp)
            .requiredSize(inputBoxSize)
            .clip(inputShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = inputShape
            )
    ) {

        KanjiBackground(Modifier.fillMaxSize())

        var shouldShowKanji by remember { mutableStateOf(true) }
        if (shouldShowKanji)
            Kanji(
                strokes = state.run { data.strokes.take(drawnStrokesCount) },
                modifier = Modifier.fillMaxSize()
            )

        val animatedStrokeProgress = remember { Animatable(initialValue = 0f) }
        val animatedStrokeData: MutableState<DrawResult?> = remember {
            mutableStateOf(null, neverEqualPolicy())
        }

        val fadingOutKanjiAlpha = remember { Animatable(initialValue = 0f) }
        var fadingOutKanjiStrokes: List<Path> by remember {
            mutableStateOf(emptyList(), neverEqualPolicy())
        }

        Kanji(
            strokes = fadingOutKanjiStrokes,
            modifier = Modifier.fillMaxSize(),
            strokeColor = MaterialTheme.colorScheme.onSurface.copy(alpha = fadingOutKanjiAlpha.value)
        )

        when (val drawResult = animatedStrokeData.value) {
            is DrawResult.Correct -> {
                val path = Path()
                path.lerpBetween(
                    drawResult.userDrawnPath,
                    drawResult.kanjiPath,
                    animatedStrokeProgress.value
                )
                Stroke(
                    path = path,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is DrawResult.Mistake -> {
                Stroke(
                    path = drawResult.path,
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Red.copy(alpha = 1 - animatedStrokeProgress.value)
                )
            }
            null -> {}
        }

        if (animatedStrokeData.value == null)
            StrokeInput(
                modifier = Modifier.fillMaxSize(),
            ) { drawnPath ->

                Logger.logMethod()

                onStrokeDrawn.invoke(DrawData(drawnPath = drawnPath))
                    .onEach {
                        animatedStrokeProgress.snapTo(0f)
                        animatedStrokeData.value = it
                        animatedStrokeProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = if (it is DrawResult.Mistake) 500 else 200
                            )
                        )
                        onAnimationCompleted(it)
                        animatedStrokeData.value = null
                        if (state.run { data.strokes.size - 1 == drawnStrokesCount } && it is DrawResult.Correct) {
                            Logger.d("fading out")
                            fadingOutKanjiStrokes = state.data.strokes
                            fadingOutKanjiAlpha.snapTo(1f)
                            if (state.progress.run { totalItems == currentItem }) {
                                shouldShowKanji = false
                            }
                            fadingOutKanjiAlpha.animateTo(0f, tween(600))

                            Logger.d("fading out completed")
                        }
                    }
                    .launchIn(coroutineScope)

            }


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