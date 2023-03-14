package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.core.kanji_data.data.buildFuriganaString
import ua.syt0r.kanji.core.kanji_data.data.withEmptyFurigana
import ua.syt0r.kanji.presentation.common.ItemHeightData
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.trackScreenHeight
import ua.syt0r.kanji.presentation.common.ui.*
import ua.syt0r.kanji.presentation.dialog.AlternativeWordsDialog
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSelectedOption
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSummaryItem
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingReviewCharacterData

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ReadingPracticeScreenUI(
    state: State<ScreenState>,
    onUpButtonClick: () -> Unit,
    onOptionSelected: (ReadingPracticeSelectedOption) -> Unit,
    onFinishButtonClick: () -> Unit
) {

    val contentBehindFabBottomPadding = remember { mutableStateOf(0.dp) }

    var resetAnswerKey by rememberSaveable { mutableStateOf(0) }
    val isShowingAnswerState = rememberSaveable(resetAnswerKey) { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        /*
         Using snapshotFlow instead of derived state to avoid buttons options being reset
         during switching to summary state
         */
        snapshotFlow { state.value }
            .filterIsInstance<ScreenState.Review>()
            .collectLatest { resetAnswerKey = it.progress.totalReviewsCount }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                state = state,
                onUpButtonClick = onUpButtonClick
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val transition = updateTransition(
                targetState = state.value to isShowingAnswerState.value,
                label = "Content Transition"
            )
            transition.AnimatedContent(
                contentKey = { (screenState, _) ->
                    screenState.let { it as? ScreenState.Review }
                        ?.progress
                        ?.totalReviewsCount
                },
                modifier = Modifier.fillMaxSize()
            ) { (screenState, isShowingAnswer) ->

                when (screenState) {
                    ScreenState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize()
                        )
                    }
                    is ScreenState.Review -> {
                        Review(
                            state = screenState,
                            isShowingAnswer = isShowingAnswer,
                            contentBottomPadding = contentBehindFabBottomPadding
                        )
                    }
                    is ScreenState.Summary -> {
                        Summary(screenState, contentBehindFabBottomPadding)
                    }
                }

            }

            Buttons(
                state = state,
                isShowingAnswerState = isShowingAnswerState,
                contentBottomPadding = contentBehindFabBottomPadding,
                showAnswerClick = { isShowingAnswerState.value = true },
                optionClick = onOptionSelected,
                onFinishButtonClick = onFinishButtonClick
            )

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    state: State<ScreenState>,
    onUpButtonClick: () -> Unit
) {

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onUpButtonClick) {
                Icon(Icons.Default.ArrowBack, null)
            }
        },
        title = {

            val progressState = remember {
                derivedStateOf { state.value.let { it as? ScreenState.Review }?.progress }
            }

            val progress = progressState.value ?: return@TopAppBar

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(align = Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToolbarCountItem(
                    count = progress.pending,
                    color = MaterialTheme.extraColorScheme.pending
                )

                ToolbarCountItem(
                    count = progress.repeat,
                    color = MaterialTheme.colorScheme.primary
                )

                ToolbarCountItem(
                    count = progress.completed,
                    color = MaterialTheme.extraColorScheme.success
                )
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun BoxScope.Buttons(
    state: State<ScreenState>,
    isShowingAnswerState: State<Boolean>,
    contentBottomPadding: MutableState<Dp>,
    showAnswerClick: () -> Unit,
    optionClick: (ReadingPracticeSelectedOption) -> Unit,
    onFinishButtonClick: () -> Unit
) {

    val updateContentPadding = { data: ItemHeightData ->
        val currentPadding = contentBottomPadding.value
        if (data.heightFromScreenBottom > currentPadding)
            contentBottomPadding.value = data.heightFromScreenBottom
    }

    val isReviewState = remember { derivedStateOf { state.value is ScreenState.Review } }
    AnimatedVisibility(
        visible = isReviewState.value,
        enter = scaleIn(),
        exit = scaleOut(),
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .trackScreenHeight(updateContentPadding)
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .clip(CircleShape)
                .height(IntrinsicSize.Min),
        ) {
            if (isShowingAnswerState.value) {
                OptionButton(
                    title = resolveString { readingPractice.goodButton },
                    onClick = { optionClick(ReadingPracticeSelectedOption.Good) },
                    containerColor = MaterialTheme.extraColorScheme.success
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
                OptionButton(
                    title = resolveString { readingPractice.repeatButton },
                    onClick = { optionClick(ReadingPracticeSelectedOption.Repeat) },
                    containerColor = MaterialTheme.colorScheme.primary
                )

            } else {
                OptionButton(
                    title = resolveString { readingPractice.showAnswerButton },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = showAnswerClick
                )
            }
        }
    }

    val isSummaryShown = remember { derivedStateOf { state.value is ScreenState.Summary } }

    AnimatedVisibility(
        visible = isSummaryShown.value,
        enter = scaleIn(),
        exit = scaleOut(),
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        ExtendedFloatingActionButton(
            onClick = onFinishButtonClick,
            text = { Text(text = resolveString { readingPractice.summaryButton }) },
            icon = { Icon(Icons.Default.Done, null) },
            modifier = Modifier.trackScreenHeight(updateContentPadding)
        )
    }

}

@Composable
private fun Review(
    state: ScreenState.Review,
    isShowingAnswer: Boolean,
    contentBottomPadding: State<Dp>,
) {

    if (LocalOrientation.current == Orientation.Portrait) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = contentBottomPadding.value + 16.dp)
        ) {
            CharacterDetails(state.characterData, isShowingAnswer)
            WordsSection(words = state.characterData.words, isShowingAnswer = isShowingAnswer)
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = contentBottomPadding.value + 16.dp)
            ) {
                CharacterDetails(data = state.characterData, showAnswer = isShowingAnswer)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 10.dp, end = 20.dp)
                    .padding(bottom = contentBottomPadding.value + 16.dp)
            ) {
                WordsSection(words = state.characterData.words, isShowingAnswer = isShowingAnswer)
            }

        }
    }

}

@Composable
private fun Summary(
    state: ScreenState.Summary,
    bottomPadding: State<Dp>
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {

        items(state.items) {
            SummaryItem(it)
        }

        item {
            Spacer(modifier = Modifier.height(bottomPadding.value + 16.dp))
        }

    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SummaryItem(
    item: ReadingPracticeSummaryItem,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val (bgColor, textColor) = when (item.repeats) {
            0 -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        }

        Text(
            text = item.character,
            fontSize = 35.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier
                .size(60.dp)
                .background(bgColor, CircleShape)
                .clip(CircleShape)
                .wrapContentSize()
                .offset(x = (-1).dp, y = (-1).dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = resolveString { readingPractice.summaryMistakesMessage(item.repeats) },
            color = when (item.repeats) {
                0 -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.primary
            },
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

    }


}

@Composable
private fun ColumnScope.CharacterDetails(
    data: ReadingReviewCharacterData,
    showAnswer: Boolean
) {

    val alpha by animateFloatAsState(targetValue = if (showAnswer) 1f else 0f)

    when (data) {
        is ReadingReviewCharacterData.Kanji -> {
            Text(
                text = data.character,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
            )


            AutoBreakRow(
                horizontalItemSpacing = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize()
                    .padding(bottom = 16.dp)
                    .alpha(alpha)
            ) {
                data.meanings.forEach { Text(text = it) }
            }

            if (data.kun.isNotEmpty()) {
                ReadingRow(
                    title = resolveString { kunyomi },
                    items = data.kun,
                    modifier = Modifier.alpha(alpha)
                )
            }

            if (data.on.isNotEmpty()) {
                ReadingRow(
                    title = resolveString { onyomi },
                    items = data.on,
                    modifier = Modifier.alpha(alpha)
                )
            }
        }
        is ReadingReviewCharacterData.Kana -> {
            Text(
                text = data.character,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize()
            )

            Text(
                text = data.reading,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .wrapContentSize()
                    .alpha(alpha)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
    }

}

@Composable
private fun ColumnScope.WordsSection(
    words: List<JapaneseWord>,
    isShowingAnswer: Boolean,
) {

    var alternativeDialogWord by remember { mutableStateOf<JapaneseWord?>(null) }
    alternativeDialogWord?.let {
        AlternativeWordsDialog(
            word = it,
            onDismissRequest = { alternativeDialogWord = null }
        )
    }

    Text(
        text = resolveString { readingPractice.words },
        modifier = Modifier.padding(vertical = 8.dp),
        style = MaterialTheme.typography.titleMedium
    )

    words.forEachIndexed { index, word ->

        val message = if (isShowingAnswer) {
            word.orderedPreview(index)
        } else {
            buildFuriganaString {
                append("${index + 1}. ")
                append(word.readings.first().withEmptyFurigana())
            }
        }

        FuriganaText(
            furiganaString = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable(enabled = isShowingAnswer, onClick = { alternativeDialogWord = word })
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )

    }

}

@Composable
private fun ReadingRow(
    title: String,
    items: List<String>,
    modifier: Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        AutoBreakRow(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            items.forEach {
                Text(
                    text = it,
                    modifier = Modifier
                        .padding(top = 4.dp, end = 4.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun RowScope.OptionButton(
    title: String,
    onClick: () -> Unit,
    containerColor: Color,
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = textColor,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .background(containerColor)
            .padding(vertical = 12.dp)
            .wrapContentSize()
    )
}

//@Preview
//@Composable
//private fun UiPreview(
//    state: ScreenState = ScreenState.Loading
//) {
//    AppTheme(useDarkTheme = false) {
//        ReadingPracticeScreenUI(
//            state = rememberUpdatedState(state),
//            onUpButtonClick = {},
//            onOptionSelected = {},
//            onFinishButtonClick = {}
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun KanaPreview() {
//    UiPreview(
//        state = ScreenState.Review(
//            progress = ReadingPracticeContract.ReviewProgress(6, 0, 0),
//            characterData = ReadingReviewCharacterData.Kana(
//                reading = "A",
//                classification = CharactersClassification.Kana.Hiragana,
//                character = "あ",
//                words = PreviewKanji.randomWords()
//            )
//        )
//    )
//}
//
//@Preview
//@Composable
//private fun KanjiPreview() {
//    UiPreview(
//        state = ScreenState.Review(
//            progress = ReadingPracticeContract.ReviewProgress(6, 0, 0),
//            characterData = ReadingReviewCharacterData.Kanji(
//                character = PreviewKanji.kanji,
//                on = PreviewKanji.on,
//                kun = PreviewKanji.kun,
//                meanings = PreviewKanji.meanings,
//                words = PreviewKanji.randomWords()
//            )
//        )
//    )
//}
//
//@Preview
//@Composable
//private fun SummaryPreview() {
//    UiPreview(
//        state = ScreenState.Summary(
//            items = (1..30).map { PreviewKanji.randomKanji() }
//                .distinct()
//                .map { ReadingPracticeSummaryItem(it, Random.nextInt(0, 4)) }
//        )
//    )
//}
//
//@Preview(device = Devices.PIXEL_C)
//@Composable
//private fun TabletPreview() {
//    KanaPreview()
//}