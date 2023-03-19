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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.core.kanji_data.data.buildFuriganaString
import ua.syt0r.kanji.core.kanji_data.data.withEmptyFurigana
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.trackItemPosition
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                state = state,
                onUpButtonClick = onUpButtonClick
            )
        }
    ) { paddingValues ->

        val transition = updateTransition(
            targetState = state.value,
            label = "Content Transition",
        )

        transition.AnimatedContent(
            contentKey = { screenState ->
                screenState.let { it as? ScreenState.Review }
                    ?.progress
                    ?.totalReviewsCount
            },
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            transitionSpec = { fadeIn() with fadeOut() }
        ) { screenState ->

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
                        onOptionSelected = onOptionSelected
                    )
                }
                is ScreenState.Summary -> {
                    Summary(
                        state = screenState,
                        onFinishButtonClick = onFinishButtonClick,
                    )
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

@Composable
private fun Review(
    state: ScreenState.Review,
    onOptionSelected: (ReadingPracticeSelectedOption) -> Unit
) {

    var shouldShowAnswer by rememberSaveable(
        key = state.progress.totalReviewsCount.toString()
    ) { mutableStateOf(false) }

    val characterDetailsContent = movableContentWithReceiverOf<ColumnScope> {
        CharacterDetails(state.characterData, shouldShowAnswer)
    }

    val wordsContent = movableContentWithReceiverOf<ColumnScope> {
        WordsSection(words = state.characterData.words, isShowingAnswer = shouldShowAnswer)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        val contentBottomPadding = remember { mutableStateOf(16.dp) }

        if (LocalOrientation.current == Orientation.Portrait) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = contentBottomPadding.value)
            ) {
                characterDetailsContent()
                wordsContent()
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
                        .padding(bottom = contentBottomPadding.value)
                ) {
                    characterDetailsContent()
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 10.dp, end = 20.dp)
                        .padding(bottom = contentBottomPadding.value)
                ) {
                    wordsContent()
                }

            }

        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .padding(horizontal = 16.dp)
                .widthIn(max = 400.dp)
                .clip(CircleShape)
                .trackItemPosition {
                    contentBottomPadding.value = it.heightFromScreenBottom + 16.dp
                }
        ) {
            if (shouldShowAnswer) {
                OptionButton(
                    title = resolveString { readingPractice.goodButton },
                    onClick = { onOptionSelected(ReadingPracticeSelectedOption.Good) },
                    containerColor = MaterialTheme.extraColorScheme.success
                )
                Box(
                    modifier = Modifier
                        .height(IntrinsicSize.Max)
                        .width(2.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
                OptionButton(
                    title = resolveString { readingPractice.repeatButton },
                    onClick = { onOptionSelected(ReadingPracticeSelectedOption.Repeat) },
                    containerColor = MaterialTheme.colorScheme.primary
                )

            } else {
                OptionButton(
                    title = resolveString { readingPractice.showAnswerButton },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { shouldShowAnswer = true }
                )
            }
        }

    }

}

@Composable
private fun Summary(
    state: ScreenState.Summary,
    onFinishButtonClick: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        val bottomPadding = remember { mutableStateOf(16.dp) }

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

        ExtendedFloatingActionButton(
            onClick = onFinishButtonClick,
            text = { Text(text = resolveString { readingPractice.summaryButton }) },
            icon = { Icon(Icons.Default.Done, null) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .trackItemPosition { bottomPadding.value = it.heightFromScreenBottom + 16.dp }
        )
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
            .clickable(onClick = onClick)
            .background(containerColor)
            .padding(vertical = 12.dp)
            .wrapContentSize()
    )
}
