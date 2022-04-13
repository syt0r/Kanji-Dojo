package ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawResult
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.KanjiData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeProgress

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun WritingPracticeScreenUI(
    screenState: ScreenState,
    onUpClick: () -> Unit,
    submitUserInput: suspend (DrawData) -> DrawResult,
    onAnimationCompleted: (DrawResult) -> Unit,
    onPracticeCompleteButtonClick: () -> Unit = {}
) {

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    when (screenState) {
                        is ScreenState.ReviewingKanji -> {
                            Row {
                                Text(
                                    text = "Review",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, false)
                                )
                                Spacer(modifier = Modifier.requiredWidth(8.dp))
                                Text(text = "${screenState.progress.currentItem}/${screenState.progress.totalItems}")
                            }
                        }
                        is ScreenState.Summary -> {
                            Text(text = "Summary")
                        }
                        else -> {}
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onUpClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) {

        val transition = updateTransition(targetState = screenState, label = "AnimatedContent")
        transition.AnimatedContent(
            transitionSpec = {
                if (targetState is ScreenState.Summary && initialState is ScreenState.ReviewingKanji) {
                    ContentTransform(
                        targetContentEnter = fadeIn(tween(600, delayMillis = 600)),
                        initialContentExit = fadeOut(tween(600, delayMillis = 600))
                    )
                } else {
                    ContentTransform(
                        targetContentEnter = fadeIn(tween(600)),
                        initialContentExit = fadeOut(tween(600))
                    )
                }
            },
            contentKey = { it.javaClass.simpleName }
        ) {
            when (it) {
                ScreenState.Loading -> {
                    Loading()
                }
                is ScreenState.ReviewingKanji -> {
                    ReviewInProgress(it, submitUserInput, onAnimationCompleted)
                }
                is ScreenState.Summary -> {
                    Summary(it, onPracticeCompleteButtonClick)
                }
            }
        }
    }

}

@Composable
private fun Loading() {

    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }

}

@Composable
private fun ReviewInProgress(
    screenState: ScreenState.ReviewingKanji,
    onStrokeDrawn: suspend (DrawData) -> DrawResult,
    onAnimationCompleted: (DrawResult) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        WritingPracticeKanjiInfoSection(
            kanjiData = screenState.data,
            modifier = Modifier.wrapContentSize(Alignment.TopCenter)
        )

        Spacer(modifier = Modifier.weight(1f))

        WritingPracticeKanjiInputSection(
            screenState,
            onStrokeDrawn,
            onAnimationCompleted
        )

    }

}

@Composable
private fun Summary(
    screenState: ScreenState.Summary,
    onPracticeCompleteButtonClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            items(
                screenState.mistakesMap.entries.toList()
            ) { (kanji, mistakes) ->

                Row {

                    Text(
                        text = kanji,
                        style = MaterialTheme.typography.displayLarge
                    )

                }

            }

        }

        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(vertical = 24.dp)
        ) {

            FilledTonalButton(onClick = onPracticeCompleteButtonClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Finish")
            }


        }

    }

}

@Preview(showBackground = true)
@Composable
private fun LoadingStatePreview() {

    AppTheme {
        WritingPracticeScreenUI(
            screenState = ScreenState.Loading,
            onUpClick = {},
            submitUserInput = { TODO() },
            onAnimationCompleted = {}
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun LoadedStatePreview() {

    AppTheme {
        WritingPracticeScreenUI(
            screenState = PreviewKanji.run {
                ScreenState.ReviewingKanji(
                    data = KanjiData(
                        kanji, (0..4).flatMap { on }, (0..10).flatMap { kun }, meanings, strokes
                    ),
                    drawnStrokesCount = 3,
                    progress = PracticeProgress(5, 1)
                )
            },
            onUpClick = {},
            submitUserInput = { TODO() },
            onAnimationCompleted = {}
        )
    }

}


@Preview(showBackground = true)
@Composable
private fun SummaryPreview() {

    AppTheme {
        WritingPracticeScreenUI(
            screenState = ScreenState.Summary(
                mistakesMap = sortedMapOf(
                    "A" to 1,
                    "B" to 0,
                    "C" to 4
                )
            ),
            onUpClick = {},
            submitUserInput = { TODO() },
            onAnimationCompleted = {}
        )
    }

}
