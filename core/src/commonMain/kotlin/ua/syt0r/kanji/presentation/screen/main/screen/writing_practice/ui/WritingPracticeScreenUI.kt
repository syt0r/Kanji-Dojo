package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.common.MultiplatformBackHandler
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.*
import ua.syt0r.kanji.presentation.common.trackScreenHeight
import ua.syt0r.kanji.presentation.common.ui.*
import ua.syt0r.kanji.presentation.dialog.AlternativeWordsDialog
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.*

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class
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
    onNextClick: (ReviewUserAction) -> Unit = {},
    toggleRadicalsHighlight: () -> Unit = {}
) {

    var shouldShowLeaveConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    if (shouldShowLeaveConfirmationDialog) {
        LeaveConfirmationDialog(
            onDismissRequest = { shouldShowLeaveConfirmationDialog = false },
            onConfirmClick = { navigateBack() }
        )
    }

    Scaffold(
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

        val transition = updateTransition(targetState = state.value, label = "AnimatedContent")
        transition.AnimatedContent(
            transitionSpec = {
                fadeIn(tween(600)) with fadeOut(tween(600))
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
                        toggleRadicalsHighlight = toggleRadicalsHighlight
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
                MultiplatformBackHandler { shouldShowLeaveConfirmationDialog = true }
            }

        }

    }

}

@Composable
private fun LeaveConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {

    MultiplatformDialog(
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
                    text = resolveString { writingPractice.leaveDialogTitle },
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                Text(
                    text = resolveString { writingPractice.leaveDialogMessage },
                    style = MaterialTheme.typography.bodyMedium
                )

                TextButton(
                    onClick = onConfirmClick,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .align(Alignment.End)
                ) {
                    Text(text = resolveString { writingPractice.leaveDialogButton })
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
                            color = MaterialTheme.extraColorScheme.pending
                        )

                        ToolbarCountItem(
                            count = screenState.progress.repeatCount,
                            color = MaterialTheme.colorScheme.primary
                        )

                        ToolbarCountItem(
                            count = screenState.progress.finishedCount,
                            color = MaterialTheme.extraColorScheme.success
                        )
                    }
                }
                is ScreenState.Summary -> {
                    Text(text = resolveString { writingPractice.summaryTitle })
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
    val rippleTheme = remember { CustomRippleTheme(colorProvider = { color }) }
    CompositionLocalProvider(LocalRippleTheme provides rippleTheme) {
        TextButton(onClick = {}) {
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
}

@Composable
private fun BottomSheetContent(
    state: State<ScreenState>,
    sheetContentHeight: State<Dp>
) {

    val characterState: State<String?> = remember {
        derivedStateOf { (state.value as? ScreenState.Review)?.data?.character }
    }
    val wordsState = remember {
        derivedStateOf(referentialEqualityPolicy()) {
            val reviewState = (state.value as? ScreenState.Review)
                ?: return@derivedStateOf emptyList()

            val list = reviewState.run {
                if (isStudyMode || drawnStrokesCount == data.strokes.size) data.words
                else data.encodedWords
            }

            list.take(WritingPracticeScreenContract.WordsLimit).mapIndexed { index, word ->
                index to word
            }
        }
    }

    var selectedWordForAlternativeDialog by rememberSaveable {
        mutableStateOf<JapaneseWord?>(null)
    }

    selectedWordForAlternativeDialog?.let {
        AlternativeWordsDialog(
            word = it,
            onDismissRequest = { selectedWordForAlternativeDialog = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(sheetContentHeight.value)
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
            text = resolveString { writingPractice.wordsBottomSheetTitle },
            modifier = Modifier
                .padding(top = 8.dp, bottom = 16.dp)
                .padding(horizontal = 20.dp),
            style = MaterialTheme.typography.titleLarge
        )


        val lazyListState = rememberLazyListState()

        LaunchedEffect(Unit) {
            snapshotFlow { characterState.value }.collectLatest {
                lazyListState.scrollToItem(0)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = lazyListState
        ) {

            items(wordsState.value) { (index, word) ->
                FuriganaText(
                    furiganaString = word.orderedPreview(index),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(onClick = { selectedWordForAlternativeDialog = word })
                        .padding(horizontal = 10.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

        }

    }

}

@Composable
private fun LoadingState() {

    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ReviewState(
    state: State<ScreenState>,
    onStrokeDrawn: suspend (DrawData) -> DrawResult,
    onAnimationCompleted: (DrawResult) -> Unit,
    onHintClick: () -> Unit,
    onNextClick: (ReviewUserAction) -> Unit,
    toggleRadicalsHighlight: () -> Unit
) {

    val infoDataState = remember {
        derivedStateOf {
            state.value.run {
                this as ScreenState.Review
                WritingPracticeInfoSectionData(
                    characterData = data,
                    isStudyMode = isStudyMode,
                    isCharacterDrawn = drawnStrokesCount == data.strokes.size,
                    shouldHighlightRadicals = shouldHighlightRadicals,
                    isNoTranslationLayout = isNoTranslationLayout
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

    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val bottomSheetHeightState = remember { mutableStateOf(100.dp) }

    val openBottomSheet: () -> Unit = {
        coroutineScope.launch { scaffoldState.bottomSheetState.expand() }
    }

    if (LocalOrientation.current == Orientation.Portrait) {

        Material3BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = { BottomSheetContent(state, bottomSheetHeightState) }
        ) {

            val infoSectionBottomPadding = remember { mutableStateOf(0.dp) }

            WritingPracticeInfoSection(
                state = infoDataState,
                bottomSheetHeight = bottomSheetHeightState,
                onExpressionsClick = openBottomSheet,
                toggleRadicalsHighlight = toggleRadicalsHighlight,
                extraBottomPaddingState = infoSectionBottomPadding,
                modifier = Modifier.fillMaxSize(),
            )

            WritingPracticeInputSection(
                state = inputDataState,
                onStrokeDrawn = onStrokeDrawn,
                onAnimationCompleted = onAnimationCompleted,
                onHintClick = onHintClick,
                onNextClick = onNextClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .trackScreenHeight {
                        infoSectionBottomPadding.value = it.heightFromScreenBottom
                    }
                    .sizeIn(maxWidth = 400.dp)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
            )

        }

    } else {

        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            Material3BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetContent = { BottomSheetContent(state, bottomSheetHeightState) },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                WritingPracticeInfoSection(
                    state = infoDataState,
                    bottomSheetHeight = bottomSheetHeightState,
                    onExpressionsClick = openBottomSheet,
                    toggleRadicalsHighlight = toggleRadicalsHighlight,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )

            WritingPracticeInputSection(
                state = inputDataState,
                onStrokeDrawn = onStrokeDrawn,
                onAnimationCompleted = onAnimationCompleted,
                onHintClick = onHintClick,
                onNextClick = onNextClick,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .wrapContentSize()
                    .sizeIn(maxWidth = 400.dp)
                    .aspectRatio(1f)
                    .padding(20.dp)
            )

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

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        val contentPaddingState = remember { mutableStateOf(16.dp) }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(120.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            items(screenState.reviewResultList) {
                SummaryItem(
                    reviewResult = it,
                    onClick = { onReviewItemClick(it) },
                    modifier = Modifier
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(contentPaddingState.value))
            }

        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp)
                .trackScreenHeight { contentPaddingState.value = it.heightFromScreenBottom }
        ) {

            FilledTonalButton(onClick = onPracticeCompleteButtonClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = resolveString { writingPractice.summaryButton })
            }

        }
    }

}

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
            text = resolveString {
                writingPractice.summaryMistakesMessage(reviewResult.characterReviewResult.mistakes)
            },
            color = when (reviewResult.reviewScore) {
                ReviewScore.Good -> MaterialTheme.colorScheme.onSurface
                ReviewScore.Bad -> MaterialTheme.colorScheme.primary
            },
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

    }


}

//@Preview(showBackground = true, locale = "ja")
//@Composable
//private fun KanjiPreview(
//    darkTheme: Boolean = false,
//    isStudyMode: Boolean = true
//) {
//    AppTheme(darkTheme) {
//        WritingPracticeScreenUI(
//            state = WritingPracticeScreenUIPreviewUtils.reviewState(
//                isKana = false,
//                isStudyMode = isStudyMode,
//                wordsCount = 10
//            )
//        )
//    }
//}
//
//@Preview(showBackground = true, heightDp = 600, locale = "ja")
//@Composable
//private fun KanjiStudyPreview() {
//    KanjiPreview(darkTheme = true, isStudyMode = true)
//}
//
//@Preview(showBackground = true)
//@Composable
//private fun KanaPreview(
//    darkTheme: Boolean = false,
//    isStudyMode: Boolean = false
//) {
//    AppTheme(darkTheme) {
//        WritingPracticeScreenUI(
//            state = WritingPracticeScreenUIPreviewUtils.reviewState(
//                isKana = true,
//                isStudyMode = isStudyMode
//            )
//        )
//    }
//}

//@Preview(showBackground = true)
//@Composable
//private fun KanaStudyPreview() {
//    KanaPreview(darkTheme = true, isStudyMode = true)
//}
//
//@Preview(showBackground = true)
//@Composable
//private fun LoadingStatePreview() {
//    AppTheme {
//        WritingPracticeScreenUI(
//            state = ScreenState.Loading.run { mutableStateOf(this) }
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//private fun SummaryPreview() {
//    AppTheme {
//        WritingPracticeScreenUI(
//            state = ScreenState.Summary.Saved(
//                reviewResultList = (0..20).map {
//                    ReviewResult(
//                        characterReviewResult = CharacterReviewResult(
//                            character = PreviewKanji.kanji,
//                            practiceId = 0,
//                            mistakes = Random.nextInt(0, 9)
//                        ),
//                        reviewScore = ReviewScore.values().random()
//                    )
//                },
//                eligibleForInAppReview = false
//            ).run { mutableStateOf(this) }
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun LeaveDialogPreview() {
//    AppTheme(useDarkTheme = true) {
//        LeaveConfirmationDialog(
//            onDismissRequest = {},
//            onConfirmClick = {}
//        )
//    }
//}
//
//@Preview(device = Devices.PIXEL_C)
//@Composable
//private fun TabletPreview() {
//    KanjiPreview(darkTheme = true)
//}
//
//object WritingPracticeScreenUIPreviewUtils {
//
//    fun reviewState(
//        isKana: Boolean = true,
//        isStudyMode: Boolean = false,
//        wordsCount: Int = 3,
//        progress: PracticeProgress = PracticeProgress(2, 2, 2),
//        drawnStrokesCount: Int = 2
//    ): State<ScreenState.Review> {
//        val words = PreviewKanji.randomWords(wordsCount)
//        return ScreenState.Review(
//            data = when {
//                isKana -> ReviewCharacterData.KanaReviewData(
//                    character = "あ",
//                    strokes = PreviewKanji.strokes,
//                    radicals = PreviewKanji.radicals,
//                    words = words,
//                    encodedWords = words,
//                    kanaSystem = CharactersClassification.Kana.Hiragana,
//                    romaji = "A"
//                )
//                else -> ReviewCharacterData.KanjiReviewData(
//                    character = PreviewKanji.kanji,
//                    strokes = PreviewKanji.strokes,
//                    radicals = PreviewKanji.radicals,
//                    words = words,
//                    encodedWords = words,
//                    kun = PreviewKanji.kun,
//                    on = PreviewKanji.on,
//                    meanings = PreviewKanji.meanings
//                )
//            },
//            isStudyMode = isStudyMode,
//            progress = progress,
//            drawnStrokesCount = drawnStrokesCount,
//            shouldHighlightRadicals = true,
//            isNoTranslationLayout = Locale.current.language == "ja"
//        ).run { mutableStateOf(this) }
//    }
//
//}