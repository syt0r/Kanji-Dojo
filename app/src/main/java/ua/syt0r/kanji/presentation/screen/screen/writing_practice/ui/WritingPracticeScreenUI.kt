package ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.kanji_data.data.FuriganaString
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.core.kanji_data.data.buildFuriganaString
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult
import ua.syt0r.kanji.presentation.common.theme.*
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.*
import ua.syt0r.kanji.common.CharactersClassification
import kotlin.random.Random

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun WritingPracticeScreenUI(
    state: State<ScreenState>,
    navigateBack: () -> Unit = {},
    submitUserInput: suspend (DrawData) -> DrawResult = { DrawResult.IgnoreCompletedPractice },
    onAnimationCompleted: (DrawResult) -> Unit = {},
    onHintClick: () -> Unit = {},
    onReviewItemClick: (ReviewResult) -> Unit = {},
    onPracticeCompleteButtonClick: () -> Unit = {},
    onNextClick: (ReviewUserAction) -> Unit = {}
) {

    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val expressionSectionLayoutCoordinates = remember {
        mutableStateOf<LayoutCoordinates?>(null)
    }

    var shouldShowLeaveConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    if (shouldShowLeaveConfirmationDialog) {
        LeaveConfirmationDialog(
            onDismissRequest = { shouldShowLeaveConfirmationDialog = false },
            onConfirmClick = { navigateBack() }
        )
    }

    BottomSheetScaffold(
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Surface {
                BottomSheetContent(
                    state = state,
                    layoutCoordinatesState = expressionSectionLayoutCoordinates
                )
            }
        },
        scaffoldState = scaffoldState,
        topBar = {
            Toolbar(
                state = state,
                onUpClick = {
                    if (state.value::class == ScreenState.Summary.Saved::class) navigateBack()
                    else shouldShowLeaveConfirmationDialog = true
                }
            )
        }
    ) { paddingValues ->

        Surface {

            val transition = updateTransition(targetState = state.value, label = "AnimatedContent")
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
                contentKey = { it::class },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                val animatedState = rememberUpdatedState(it)
                val stateClass by remember { derivedStateOf { animatedState.value::class } }

                when (stateClass) {
                    ScreenState.Review::class -> {
                        ReviewState(
                            state = animatedState,
                            onStrokeDrawn = submitUserInput,
                            onAnimationCompleted = onAnimationCompleted,
                            onHintClick = onHintClick,
                            onNextClick = onNextClick,
                            onExpressionSectionPlaced = { coordinates ->
                                expressionSectionLayoutCoordinates.value = coordinates
                            },
                            onExpressionClick = {
                                coroutineScope.launch { scaffoldState.bottomSheetState.expand() }
                            }
                        )
                    }
                    ScreenState.Loading::class,
                    ScreenState.Summary.Saving::class -> {
                        LoadingState()
                    }
                    ScreenState.Summary.Saved::class -> {
                        SummaryState(
                            state = animatedState,
                            onReviewItemClick = onReviewItemClick,
                            onPracticeCompleteButtonClick = onPracticeCompleteButtonClick
                        )
                    }
                    else -> throw IllegalStateException("Unhandled state[$stateClass]")
                }

                val shouldHandleBackClicksState = remember {
                    derivedStateOf { stateClass != ScreenState.Summary.Saved::class }
                }
                if (shouldHandleBackClicksState.value) {
                    BackHandler { shouldShowLeaveConfirmationDialog = true }
                }

            }
        }

    }

}

@Composable
private fun LeaveConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismissRequest
    ) {

        Surface(
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .padding(20.dp)
            ) {

                Text(
                    text = stringResource(R.string.writing_practice_leave_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                Text(
                    text = stringResource(R.string.writing_practice_leave_dialog_description),
                    style = MaterialTheme.typography.bodyMedium
                )

                TextButton(
                    onClick = onConfirmClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .align(Alignment.End)
                ) {
                    Text(text = stringResource(R.string.writing_practice_leave_dialog_confirm))
                }

            }
        }


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    state: State<ScreenState>,
    onUpClick: () -> Unit
) {
    TopAppBar(
        title = {
            when (val screenState = state.value) {
                is ScreenState.Review -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(align = Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ToolbarCountItem(
                            count = screenState.progress.pendingCount,
                            color = MaterialTheme.colorScheme.pendingColor()
                        )

                        ToolbarCountItem(
                            count = screenState.progress.repeatCount,
                            color = MaterialTheme.colorScheme.primary
                        )

                        ToolbarCountItem(
                            count = screenState.progress.finishedCount,
                            color = MaterialTheme.colorScheme.successColor()
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
private fun BottomSheetContent(
    state: State<ScreenState>,
    layoutCoordinatesState: State<LayoutCoordinates?>
) {

    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val screenDensity = LocalDensity.current.density

    val sheetContentHeight by remember {
        derivedStateOf {
            layoutCoordinatesState.value
                ?.boundsInRoot()
                ?.let { screenHeightDp - it.top / screenDensity }
                ?.takeIf { it > 200f }
                ?.dp
                ?: screenHeightDp.dp
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(sheetContentHeight)
    ) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline)
                    .size(width = 60.dp, height = 4.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = stringResource(R.string.writing_practice_bottom_sheet_title),
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                style = MaterialTheme.typography.titleLarge
            )


            val reviewState by remember {
                derivedStateOf { state.value as? ScreenState.Review }
            }
            val currentCharacter = reviewState?.data?.character
            val isCharacterFullyDrawn = reviewState
                ?.run { drawnStrokesCount == data.strokes.size } == true

            val words: List<FuriganaString>? = remember(currentCharacter to isCharacterFullyDrawn) {
                reviewState
                    ?.let {
                        if (it.isStudyMode || it.drawnStrokesCount == it.data.strokes.size) it.data.words
                        else it.data.encodedWords
                    }
                    ?.mapIndexed { index, japaneseWord ->
                        buildFuriganaString {
                            append("${index + 1}. ")
                            append(japaneseWord.furiganaString)
                            append(" - ")
                            append(japaneseWord.meanings.first())
                        }
                    }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                words?.let {
                    items(it) { string -> FuriganaText(furiganaString = string) }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }

            }

        }

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
    state: State<ScreenState>,
    onStrokeDrawn: suspend (DrawData) -> DrawResult,
    onAnimationCompleted: (DrawResult) -> Unit,
    onHintClick: () -> Unit,
    onNextClick: (ReviewUserAction) -> Unit,
    onExpressionSectionPlaced: (LayoutCoordinates) -> Unit,
    onExpressionClick: () -> Unit
) {

    val infoSectionScrollResetKey by remember {
        derivedStateOf { state.value.let { it as ScreenState.Review }.data.character }
    }
    val infoSectionScrollState = remember(infoSectionScrollResetKey) { ScrollState(0) }

    val infoDataState = remember {
        derivedStateOf {
            state.value.run {
                this as ScreenState.Review
                WritingPracticeInfoSectionData(
                    data,
                    isStudyMode,
                    drawnStrokesCount == data.strokes.size
                )
            }
        }
    }

    val inputDataState = remember {
        derivedStateOf(structuralEqualityPolicy()) {
            state.value.run {
                this as ScreenState.Review
                WritingPracticeInputSectionData(
                    strokes = data.strokes,
                    drawnStrokesCount = drawnStrokesCount,
                    isStudyMode = isStudyMode,
                    totalMistakes = currentCharacterMistakes
                )
            }
        }
    }

    when (LocalConfiguration.current.orientation) {

        Configuration.ORIENTATION_PORTRAIT -> {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                WritingPracticeInfoSection(
                    state = infoDataState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(infoSectionScrollState)
                        .padding(20.dp)
                        .weight(1f),
                    onExpressionsSectionPositioned = onExpressionSectionPlaced,
                    onExpressionsClick = onExpressionClick
                )

                WritingPracticeInputSection(
                    state = inputDataState,
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
                    state = infoDataState,
                    modifier = Modifier
                        .verticalScroll(infoSectionScrollState)
                        .weight(1f)
                        .padding(20.dp),
                    onExpressionsSectionPositioned = onExpressionSectionPlaced,
                    onExpressionsClick = onExpressionClick
                )

                WritingPracticeInputSection(
                    state = inputDataState,
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
    state: State<ScreenState>,
    onReviewItemClick: (ReviewResult) -> Unit,
    onPracticeCompleteButtonClick: () -> Unit
) {

    val screenState = state.value as ScreenState.Summary.Saved

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
private fun KanjiPreview(
    darkTheme: Boolean = false,
    isStudyMode: Boolean = false
) {
    AppTheme(darkTheme) {
        WritingPracticeScreenUI(
            state = WritingPracticeScreenUIPreviewUtils.reviewState(isKana = false)
        )
    }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
private fun KanjiStudyPreview() {
    KanjiPreview(darkTheme = true, isStudyMode = true)
}

@Preview(showBackground = true)
@Composable
private fun KanaPreview(
    darkTheme: Boolean = false,
    isStudyMode: Boolean = false
) {
    AppTheme(darkTheme) {
        WritingPracticeScreenUI(
            state = WritingPracticeScreenUIPreviewUtils.reviewState(
                isKana = true,
                isStudyMode = isStudyMode
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KanaStudyPreview() {
    KanaPreview(darkTheme = true, isStudyMode = true)
}

@Preview(showBackground = true)
@Composable
private fun LoadingStatePreview() {
    AppTheme {
        WritingPracticeScreenUI(
            state = ScreenState.Loading.run { mutableStateOf(this) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SummaryPreview() {
    AppTheme {
        WritingPracticeScreenUI(
            state = ScreenState.Summary.Saved(
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
            ).run { mutableStateOf(this) }
        )
    }
}

@Preview
@Composable
private fun LeaveDialogPreview() {
    AppTheme(useDarkTheme = true) {
        LeaveConfirmationDialog(
            onDismissRequest = {},
            onConfirmClick = {}
        )
    }
}

object WritingPracticeScreenUIPreviewUtils {

    fun reviewState(
        isKana: Boolean = true,
        isStudyMode: Boolean = false,
        wordsCount: Int = 3,
        progress: PracticeProgress = PracticeProgress(2, 2, 2),
        drawnStrokesCount: Int = 2
    ): State<ScreenState.Review> {
        val words = (0 until wordsCount).map {
            JapaneseWord(
                furiganaString = buildFuriganaString {
                    append("イランコントラ")
                    append("事", "じ")
                    append("件", "けん")
                },
                meanings = listOf("Test meaning")
            )
        }
        return ScreenState.Review(
            data = when {
                isKana -> ReviewCharacterData.KanaReviewData(
                    character = PreviewKanji.kanji,
                    strokes = PreviewKanji.strokes,
                    words = words,
                    encodedWords = words,
                    kanaSystem = CharactersClassification.Kana.HIRAGANA,
                    romaji = "A"
                )
                else -> ReviewCharacterData.KanjiReviewData(
                    character = PreviewKanji.kanji,
                    strokes = PreviewKanji.strokes,
                    words = words,
                    encodedWords = words,
                    kun = PreviewKanji.kun,
                    on = PreviewKanji.on,
                    meanings = PreviewKanji.meanings
                )
            },
            isStudyMode = isStudyMode,
            progress = progress,
            drawnStrokesCount = drawnStrokesCount
        ).run { mutableStateOf(this) }
    }

}