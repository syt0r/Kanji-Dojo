package ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.*
import ua.syt0r.kanji_dojo.shared.CharactersClassification
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun WritingPracticeScreenUI(
    screenState: ScreenState,
    onUpClick: () -> Unit = {},
    submitUserInput: suspend (DrawData) -> DrawResult = { DrawResult.IgnoreCompletedPractice },
    onAnimationCompleted: (DrawResult) -> Unit = {},
    onHintClick: () -> Unit = {},
    onReviewItemClick: (ReviewResult) -> Unit = {},
    onPracticeCompleteButtonClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {

    Scaffold(
        topBar = {
            Toolbar(
                screenState = screenState,
                onUpClick = onUpClick
            )
        }
    ) { paddingValues ->

        val transition = updateTransition(targetState = screenState, label = "AnimatedContent")
        transition.AnimatedContent(
            transitionSpec = {
                if (targetState is ScreenState.Summary.Saved && initialState is ScreenState.Summary.Saving) {
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
            contentKey = { it.javaClass.simpleName },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (it) {
                ScreenState.Loading, ScreenState.Summary.Saving -> {
                    LoadingState()
                }
                is ScreenState.Review -> {
                    ReviewState(
                        screenState = it,
                        onStrokeDrawn = submitUserInput,
                        onAnimationCompleted = onAnimationCompleted,
                        onHintClick = onHintClick,
                        onNextClick = onNextClick
                    )
                }
                is ScreenState.Summary.Saved -> {
                    SummaryState(
                        screenState = it,
                        onReviewItemClick = onReviewItemClick,
                        onPracticeCompleteButtonClick = onPracticeCompleteButtonClick
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    screenState: ScreenState,
    onUpClick: () -> Unit
) {
    TopAppBar(
        title = {
            when (screenState) {
                is ScreenState.Review -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(align = Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ToolbarCountItem(
                            count = screenState.progress.pendingCount,
                            color = Color.LightGray
                        )

                        ToolbarCountItem(
                            count = screenState.progress.repeatCount,
                            color = Color.Red
                        )

                        ToolbarCountItem(
                            count = screenState.progress.finishedCount,
                            color = Color.Green
                        )
                    }
                }
                is ScreenState.Summary -> {
                    Text(text = stringResource(R.string.writing_practice_summary_title))
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

@Composable
private fun ToolbarCountItem(count: Int, color: Color) {
    TextButton(
        onClick = { /*TODO*/ }
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = count.toString(), color = color)
    }
}

@Composable
private fun LoadingState() {

    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }

}

@SuppressLint("SwitchIntDef")
@Composable
private fun ReviewState(
    screenState: ScreenState.Review,
    onStrokeDrawn: suspend (DrawData) -> DrawResult,
    onAnimationCompleted: (DrawResult) -> Unit,
    onHintClick: () -> Unit,
    onNextClick: () -> Unit
) {

    val configuration = LocalConfiguration.current
    when (configuration.orientation) {

        Configuration.ORIENTATION_PORTRAIT -> {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                WritingPracticeInfoSection(
                    reviewCharacterData = screenState.data,
                    isStudyMode = screenState.isStudyMode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .weight(1f)
                )

                WritingPracticeInputSection(
                    strokes = screenState.data.strokes,
                    drawnStrokesCount = screenState.drawnStrokesCount,
                    isStudyMode = screenState.isStudyMode,
                    onStrokeDrawn = onStrokeDrawn,
                    onAnimationCompleted = onAnimationCompleted,
                    onHintClick = onHintClick,
                    onNextClick = onNextClick,
                    modifier = Modifier
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .padding(20.dp)
                )

            }
        }

        Configuration.ORIENTATION_LANDSCAPE -> {

            Row(
                modifier = Modifier.fillMaxSize()
            ) {

                WritingPracticeInfoSection(
                    reviewCharacterData = screenState.data,
                    isStudyMode = screenState.isStudyMode,
                    modifier = Modifier
                        .weight(1f)
                        .padding(20.dp)
                )

                WritingPracticeInputSection(
                    strokes = screenState.data.strokes,
                    drawnStrokesCount = screenState.drawnStrokesCount,
                    isStudyMode = screenState.isStudyMode,
                    onStrokeDrawn = onStrokeDrawn,
                    onAnimationCompleted = onAnimationCompleted,
                    onHintClick = onHintClick,
                    onNextClick = onNextClick,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(20.dp)
                )

            }
        }
    }

}

@Composable
private fun SummaryState(
    screenState: ScreenState.Summary.Saved,
    onReviewItemClick: (ReviewResult) -> Unit,
    onPracticeCompleteButtonClick: () -> Unit
) {

    val summaryCharacterItemSizeDp = 120
    val columns = LocalConfiguration.current.screenWidthDp / summaryCharacterItemSizeDp

    val listData = remember { screenState.reviewResultList.chunked(columns) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            items(listData) { reviewItems ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {

                    reviewItems.forEach {
                        key(it.characterReviewResult.character) {
                            SummaryItem(
                                reviewResult = it,
                                onClick = { onReviewItemClick(it) },
                                modifier = Modifier
                                    .width(summaryCharacterItemSizeDp.dp)
                                    .height(IntrinsicSize.Max)
                            )
                        }
                    }

                    if (reviewItems.size < columns) {
                        val emptySpaceItems = columns - reviewItems.size
                        Spacer(modifier = Modifier.width(summaryCharacterItemSizeDp.dp * emptySpaceItems))
                    }

                }

            }

            item { Spacer(modifier = Modifier.height(100.dp)) }

        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp)
        ) {

            FilledTonalButton(onClick = onPracticeCompleteButtonClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = stringResource(R.string.writing_practice_summary_finish_button))
            }


        }

    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SummaryItem(
    reviewResult: ReviewResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val (bgColor, textColor) = when (reviewResult.reviewScore) {
            ReviewScore.Good -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
            ReviewScore.Bad -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        }

        Text(
            text = reviewResult.characterReviewResult.character,
            fontSize = 35.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier
                .size(60.dp)
                .background(bgColor, CircleShape)
                .clip(CircleShape)
                .clickable(onClick = onClick)
                .wrapContentSize()
                .offset(x = (-1).dp, y = (-1).dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = pluralStringResource(
                R.plurals.writing_practice_summary_mistakes,
                reviewResult.characterReviewResult.mistakes,
                reviewResult.characterReviewResult.mistakes
            ),
            color = when (reviewResult.reviewScore) {
                ReviewScore.Good -> MaterialTheme.colorScheme.onSurface
                ReviewScore.Bad -> MaterialTheme.colorScheme.primary
            },
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

    }


}

@Preview(showBackground = true)
@Composable
private fun KanjiPreview(isStudyMode: Boolean = false) {
    AppTheme {
        WritingPracticeScreenUI(
            screenState = PreviewKanji.run {
                ScreenState.Review(
                    data = ReviewCharacterData.KanjiReviewData(
                        kanji,
                        strokes,
                        (0..4).flatMap { on },
                        (0..10).flatMap { kun },
                        meanings
                    ),
                    isStudyMode = isStudyMode,
                    drawnStrokesCount = 3,
                    progress = PracticeProgress(5, 1, 0)
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KanjiStudyPreview() {
    KanjiPreview(true)
}

@Preview(showBackground = true)
@Composable
private fun KanaPreview(isStudyMode: Boolean = false) {
    AppTheme {
        WritingPracticeScreenUI(
            screenState = PreviewKanji.run {
                ScreenState.Review(
                    data = ReviewCharacterData.KanaReviewData(
                        kanji,
                        strokes,
                        kanaSystem = CharactersClassification.Kana.HIRAGANA,
                        romaji = "A"
                    ),
                    isStudyMode = isStudyMode,
                    drawnStrokesCount = 3,
                    progress = PracticeProgress(5, 1, 0)
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KanaStudyPreview() {
    KanaPreview(true)
}

@Preview(showBackground = true)
@Composable
private fun LoadingStatePreview() {
    AppTheme {
        WritingPracticeScreenUI(
            screenState = ScreenState.Loading
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SummaryPreview() {
    AppTheme {
        WritingPracticeScreenUI(
            screenState = ScreenState.Summary.Saved(
                reviewResultList = (0..20).map {
                    ReviewResult(
                        characterReviewResult = CharacterReviewResult(
                            character = PreviewKanji.kanji,
                            practiceId = 0,
                            mistakes = Random.nextInt(0, 9)
                        ),
                        reviewScore = ReviewScore.values().random()
                    )
                },
                eligibleForInAppReview = false
            )
        )
    }
}
