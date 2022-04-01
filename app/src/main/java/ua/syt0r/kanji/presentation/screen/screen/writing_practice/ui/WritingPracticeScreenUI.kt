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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawResult
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.KanjiData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeProgress

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalAnimationApi::class)
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
                title = {
                    when (state) {
                        is State.ReviewingKanji -> {
                            Row {
                                Text(
                                    text = "Review",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, false)
                                )
                                Spacer(modifier = Modifier.requiredWidth(4.dp))
                                Text(text = "${state.progress.currentItem}/${state.progress.totalItems}")
                            }
                        }
                        is State.Summary -> {
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

        val transition = updateTransition(targetState = state, label = "AnimatedContent")
        transition.AnimatedContent(
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = fadeIn(tween(1000)),
                    initialContentExit = fadeOut(tween(1000))
                )
            },
            contentKey = { state.javaClass.simpleName }
        ) {
            when (it) {
                State.Init,
                State.Loading -> {
                    Loading()
                }
                is State.ReviewingKanji -> {
                    ReviewInProgress(it, submitUserInput, onAnimationCompleted)
                }
                is State.Summary -> {
                    Summary(it)
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
    state: State.ReviewingKanji,
    onStrokeDrawn: (DrawData) -> Flow<DrawResult>,
    onAnimationCompleted: (DrawResult) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        WritingPracticeKanjiInfoSection(
            kanjiData = state.data,
            modifier = Modifier.wrapContentSize(Alignment.TopCenter)
        )

        Spacer(modifier = Modifier.weight(1f))

        WritingPracticeKanjiInputSection(
            state,
            onStrokeDrawn,
            onAnimationCompleted
        )

    }

}

@Composable
private fun Summary(state: State.Summary) {

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
                state.mistakesMap.entries.toList()
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


            TextButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Repeat")
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(onClick = { /*TODO*/ }) {
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
                    data = KanjiData(
                        kanji, (0..4).flatMap { on }, (0..10).flatMap { kun }, meanings, strokes
                    ),
                    drawnStrokesCount = 3,
                    progress = PracticeProgress(5, 1)
                )
            },
            onUpClick = {},
            submitUserInput = { flow { } },
            onAnimationCompleted = {}
        )
    }

}


@Preview(showBackground = true)
@Composable
private fun SummaryPreview() {

    AppTheme {
        WritingPracticeScreenUI(
            state = State.Summary(
                mistakesMap = sortedMapOf(
                    "A" to 1,
                    "B" to 0,
                    "C" to 4
                )
            ),
            onUpClick = {},
            submitUserInput = { flow { } },
            onAnimationCompleted = {}
        )
    }

}
