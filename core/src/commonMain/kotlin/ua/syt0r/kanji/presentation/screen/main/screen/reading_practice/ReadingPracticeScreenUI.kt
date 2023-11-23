package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.core.kanji_data.data.buildFuriganaString
import ua.syt0r.kanji.core.kanji_data.data.withEmptyFurigana
import ua.syt0r.kanji.presentation.common.MultiplatformBackHandler
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.trackItemPosition
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.LocalOrientation
import ua.syt0r.kanji.presentation.common.ui.Orientation
import ua.syt0r.kanji.presentation.dialog.AlternativeWordsDialog
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeConfigurationCharactersSelection
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeConfigurationContainer
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeLeaveConfirmationDialog
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeSavedState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeSavingResult
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeSavingState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeToolbar
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeToolbarState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.rememberPracticeConfigurationCharactersSelectionState
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSelectedOption
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingReviewCharacterData
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingScreenConfiguration

@Composable
fun ReadingPracticeScreenUI(
    state: State<ScreenState>,
    navigateBack: () -> Unit,
    onConfigured: (ReadingScreenConfiguration) -> Unit,
    onOptionSelected: (ReadingPracticeSelectedOption) -> Unit,
    onPracticeSaveClick: (PracticeSavingResult) -> Unit,
    onFinishButtonClick: () -> Unit
) {

    var shouldShowLeaveConfirmationDialog by rememberSaveable { mutableStateOf(false) }
    if (shouldShowLeaveConfirmationDialog) {
        PracticeLeaveConfirmationDialog(
            onDismissRequest = { shouldShowLeaveConfirmationDialog = false },
            onConfirmClick = { navigateBack() }
        )
    }

    val shouldShowLeaveConfirmationOnBackClick = remember {
        derivedStateOf {
            state.value.let { !(it is ScreenState.Configuration || it is ScreenState.Saved) }
        }
    }

    if (shouldShowLeaveConfirmationOnBackClick.value) {
        MultiplatformBackHandler { shouldShowLeaveConfirmationDialog = true }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            PracticeToolbar(
                state = state.toToolbarState(),
                onUpButtonClick = {
                    if (shouldShowLeaveConfirmationOnBackClick.value) {
                        shouldShowLeaveConfirmationDialog = true
                    } else {
                        navigateBack()
                    }
                }
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
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { screenState ->

            when (screenState) {
                ScreenState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }

                is ScreenState.Configuration -> {
                    val config = rememberPracticeConfigurationCharactersSelectionState(
                        characters = screenState.characters,
                        shuffle = true
                    )
                    PracticeConfigurationContainer(
                        onClick = {
                            onConfigured(
                                ReadingScreenConfiguration(
                                    characters = config.result,
                                    shuffle = config.selectedShuffle.value
                                )
                            )
                        }
                    ) {
                        PracticeConfigurationCharactersSelection(config)
                    }
                }

                is ScreenState.Review -> {
                    Review(
                        state = screenState,
                        onOptionSelected = onOptionSelected
                    )
                }

                is ScreenState.Saving -> {
                    PracticeSavingState(
                        defaultToleratedMistakesCount = screenState.outcomeSelectionConfiguration
                            .toleratedMistakesCount,
                        reviewResults = screenState.reviewResultList,
                        onSaveClick = onPracticeSaveClick
                    )
                }

                is ScreenState.Saved -> {
                    PracticeSavedState(
                        charactersReviewed = screenState.run { goodCharacters.size + repeatCharacters.size },
                        practiceDuration = screenState.practiceDuration,
                        accuracy = screenState.accuracy,
                        failedCharacters = screenState.repeatCharacters,
                        goodCharacters = screenState.goodCharacters,
                        onFinishClick = onFinishButtonClick
                    )
                }
            }

        }

    }

}

@Composable
private fun State<ScreenState>.toToolbarState(): State<PracticeToolbarState> {
    return remember {
        derivedStateOf {
            when (val currentValue = this.value) {
                ScreenState.Loading -> PracticeToolbarState.Loading
                is ScreenState.Configuration -> PracticeToolbarState.Configuration
                is ScreenState.Review -> currentValue.progress.run {
                    PracticeToolbarState.Review(pending, repeat, completed)
                }

                is ScreenState.Saving -> PracticeToolbarState.Saving
                is ScreenState.Saved -> PracticeToolbarState.Saved
            }
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
                    title = resolveString { readingPractice.repeatButton },
                    onClick = { onOptionSelected(ReadingPracticeSelectedOption.Repeat) },
                    containerColor = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier
                        .height(IntrinsicSize.Max)
                        .width(2.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
                OptionButton(
                    title = resolveString { readingPractice.goodButton },
                    onClick = { onOptionSelected(ReadingPracticeSelectedOption.Good) },
                    containerColor = MaterialTheme.extraColorScheme.success
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
                .heightIn(min = 50.dp)
                .padding(horizontal = 10.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable(enabled = isShowingAnswer, onClick = { alternativeDialogWord = word })
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .wrapContentSize(Alignment.CenterStart)
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
