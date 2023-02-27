package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import androidx.annotation.StringRes
import androidx.compose.animation.*
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.core.kanji_data.data.buildFuriganaString
import ua.syt0r.kanji.core.kanji_data.data.withEmptyFurigana
import ua.syt0r.kanji.presentation.common.onHeightFromScreenBottomFound
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.CustomRippleTheme
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.furiganaStringResource
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSelectedOption
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSummaryItem
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ReadingPracticeScreenUI(
    state: State<ScreenState>,
    onUpButtonClick: () -> Unit,
    onOptionSelected: (ReadingPracticeSelectedOption) -> Unit,
    onFinishButtonClick: () -> Unit
) {

    val contentBehindFabBottomPadding = remember { mutableStateOf(0.dp) }

    Scaffold(
        topBar = {
            Toolbar(
                state = state,
                onUpButtonClick = onUpButtonClick
            )
        },
        floatingActionButton = {
            val shouldShowButton by remember {
                derivedStateOf { state.value::class == ScreenState.Summary::class }
            }
            AnimatedVisibility(
                visible = shouldShowButton,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = onFinishButtonClick,
                    text = { Text(text = stringResource(R.string.reading_practice_finish)) },
                    icon = { Icon(Icons.Default.Done, null) },
                    modifier = Modifier.onHeightFromScreenBottomFound {
                        contentBehindFabBottomPadding.value = it + 16.dp
                    }
                )
            }
        }
    ) {

        AnimatedContent(
            targetState = state.value,
            modifier = Modifier.padding(it),
            transitionSpec = {
                if (initialState == ScreenState.Loading || targetState == ScreenState.Loading) {
                    fadeIn() with fadeOut()
                } else {
                    slideInHorizontally() with slideOutHorizontally()
                }
            }
        ) { screenState ->

            when (screenState) {
                ScreenState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
                is ScreenState.Loaded.KanaReview -> {
                    Kana(screenState, onOptionSelected)
                }
                is ScreenState.Loaded.KanjiReview -> {
                    Kanji(screenState, onOptionSelected)
                }
                is ScreenState.Summary -> {
                    Summary(screenState, contentBehindFabBottomPadding)
                }
            }

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
                derivedStateOf { state.value.let { it as? ScreenState.Loaded }?.progress }
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

@Composable
private fun Kana(
    state: ScreenState.Loaded.KanaReview,
    onOptionSelected: (ReadingPracticeSelectedOption) -> Unit
) {

    Column(modifier = Modifier.padding(20.dp)) {

        val isShowingAnswerState = rememberSaveable(state) { mutableStateOf(false) }

        Text(
            text = state.character,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
        )

        if (isShowingAnswerState.value) {
            Text(
                text = state.reading,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .wrapContentSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        OptionsButtonsRow(
            isShowingAnswerState = isShowingAnswerState,
            showAnswerClick = { isShowingAnswerState.value = true },
            optionClick = onOptionSelected
        )

    }

}

@Composable
private fun Kanji(
    state: ScreenState.Loaded.KanjiReview,
    onOptionSelected: (ReadingPracticeSelectedOption) -> Unit
) {

    Column(modifier = Modifier.padding(20.dp)) {

        val isShowingAnswerState = rememberSaveable(state) { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = state.character,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 20.dp)
            )

            KanjiDetails(state, isShowingAnswerState.value)

        }

        OptionsButtonsRow(
            isShowingAnswerState = isShowingAnswerState,
            showAnswerClick = { isShowingAnswerState.value = true },
            optionClick = onOptionSelected
        )

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
            Spacer(modifier = Modifier.height(bottomPadding.value))
        }

    }
}


@Composable
private fun SummaryItem(
    item: ReadingPracticeSummaryItem,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val (bgColor, textColor) = when {
            item.repeats <= 2 -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
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
            text = pluralStringResource(
                R.plurals.writing_practice_summary_mistakes,
                item.repeats,
                item.repeats
            ),
            color = when {
                item.repeats <= 2 -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.primary
            },
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

    }


}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KanjiDetails(
    state: ScreenState.Loaded.KanjiReview,
    showAnswer: Boolean
) {

    if (showAnswer) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .padding(bottom = 16.dp)
        ) {
            state.meanings.forEach { Text(text = it) }
        }
    }

    if (showAnswer && state.kun.isNotEmpty()) {
        ReadingRow(
            titleResId = R.string.kanji_info_kun,
            items = state.kun
        )
    }

    if (showAnswer && state.on.isNotEmpty()) {
        ReadingRow(
            titleResId = R.string.kanji_info_on,
            items = state.on
        )
    }

    Text(
        text = stringResource(R.string.reading_practice_words),
        modifier = Modifier.padding(vertical = 8.dp),
        style = MaterialTheme.typography.titleMedium
    )

    state.words.forEachIndexed { index, word ->

        val message = if (showAnswer) {
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
                .padding(start = 20.dp)
        )

    }

}

@Composable
private fun ReadingRow(
    @StringRes titleResId: Int,
    items: List<String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FuriganaText(
            furiganaString = furiganaStringResource(titleResId),
            textStyle = MaterialTheme.typography.titleMedium,
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
private fun OptionsButtonsRow(
    isShowingAnswerState: State<Boolean>,
    showAnswerClick: () -> Unit,
    optionClick: (ReadingPracticeSelectedOption) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .widthIn(max = 400.dp)
            .clip(CircleShape)
            .height(IntrinsicSize.Min),
    ) {
        if (isShowingAnswerState.value) {
            val dividerModifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .background(MaterialTheme.colorScheme.surface)

            OptionButton(
                title = stringResource(R.string.reading_practice_good),
                onClick = { optionClick(ReadingPracticeSelectedOption.Good) },
                containerColor = MaterialTheme.extraColorScheme.success
            )
            Box(modifier = dividerModifier)
            OptionButton(
                title = stringResource(R.string.reading_practice_hard),
                onClick = { optionClick(ReadingPracticeSelectedOption.Hard) },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(modifier = dividerModifier)
            OptionButton(
                title = stringResource(R.string.reading_practice_repeat),
                onClick = { optionClick(ReadingPracticeSelectedOption.Repeat) },
                containerColor = MaterialTheme.colorScheme.primary
            )

        } else {
            OptionButton(
                title = stringResource(R.string.reading_practice_show_answer),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = showAnswerClick
            )
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

@Preview
@Composable
private fun UiPreview(
    state: ScreenState = ScreenState.Loading
) {
    AppTheme(useDarkTheme = false) {
        ReadingPracticeScreenUI(
            state = rememberUpdatedState(state),
            onUpButtonClick = {},
            onOptionSelected = {},
            onFinishButtonClick = {}
        )
    }
}

@Preview
@Composable
private fun KanaPreview() {
    UiPreview(
        state = ScreenState.Loaded.KanaReview(
            progress = ReadingPracticeContract.ReviewProgress(6, 0, 0),
            reading = "A",
            classification = CharactersClassification.Kana.Hiragana,
            character = "あ"
        )
    )
}

@Preview
@Composable
private fun KanjiPreview() {
    UiPreview(
        state = ScreenState.Loaded.KanjiReview(
            progress = ReadingPracticeContract.ReviewProgress(6, 0, 0),
            character = PreviewKanji.randomKanji(),
            on = PreviewKanji.on,
            kun = PreviewKanji.kun,
            meanings = PreviewKanji.meanings,
            words = PreviewKanji.randomWords(),
            encodedWords = PreviewKanji.randomWords()
        )
    )
}

@Preview
@Composable
private fun SummaryPreview() {
    UiPreview(
        state = ScreenState.Summary(
            items = (1..30).map { PreviewKanji.randomKanji() }
                .distinct()
                .map { ReadingPracticeSummaryItem(it, Random.nextInt(0, 4)) }
        )
    )
}

@Preview(device = Devices.PIXEL_C)
@Composable
private fun TabletPreview() {
    KanaPreview()
}