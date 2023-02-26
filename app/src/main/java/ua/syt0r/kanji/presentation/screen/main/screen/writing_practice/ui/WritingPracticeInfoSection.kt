package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.core.kanji_data.data.buildFuriganaString
import ua.syt0r.kanji.presentation.common.onHeightFromScreenBottomFound
import ua.syt0r.kanji.presentation.common.stringResource
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.MostlySingleLineEliminateOverflowRow
import ua.syt0r.kanji.presentation.common.ui.furiganaStringResource
import ua.syt0r.kanji.presentation.common.ui.kanji.Kanji
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.common.ui.kanji.RadicalKanji
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewCharacterData

private const val NoTranslationLayoutPreviewWordsLimit = 5

data class WritingPracticeInfoSectionData(
    val characterData: ReviewCharacterData,
    val isStudyMode: Boolean,
    val isCharacterDrawn: Boolean,
    val shouldHighlightRadicals: Boolean,
    val isNoTranslationLayout: Boolean
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WritingPracticeInfoSection(
    state: State<WritingPracticeInfoSectionData>,
    modifier: Modifier = Modifier,
    expressionPreviewTopPositionState: MutableState<Dp>,
    onExpressionsClick: () -> Unit = {},
    toggleRadicalsHighlight: () -> Unit = {},
    extraBottomPaddingState: State<Dp> = rememberUpdatedState(0.dp)
) {

    val transition = updateTransition(
        targetState = state.value,
        label = "Content Change Transition"
    )
    transition.AnimatedContent(
        contentKey = { it.characterData.character to it.isStudyMode },
        modifier = modifier,
        transitionSpec = {
            slideInHorizontally(tween(600)) + fadeIn(tween(600)) with
                    slideOutHorizontally(tween(600)) + fadeOut(tween(600)) using
                    SizeTransform(clip = false)
        }
    ) { data ->

        val scrollStateResetKey = data.run { characterData.character to isStudyMode }
        val scrollState = remember(scrollStateResetKey) { ScrollState(0) }
        val isNoTranslationLayout = data.isNoTranslationLayout

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {

            val charData = data.characterData
            val isKanaReview = charData is ReviewCharacterData.KanaReviewData

            when {
                (isNoTranslationLayout || isKanaReview) && data.isStudyMode -> {
                    AnimatedCharacterSection(
                        data = data,
                        toggleRadicalsHighlight = toggleRadicalsHighlight,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp)
                    )
                }
                !isNoTranslationLayout && charData is ReviewCharacterData.KanjiReviewData -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize()
                            .padding(bottom = 16.dp)
                    ) {

                        if (data.isStudyMode) {
                            AnimatedCharacterSection(
                                data = data,
                                toggleRadicalsHighlight = toggleRadicalsHighlight,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }

                        KanjiMeanings(
                            meanings = charData.meanings,
                            modifier = Modifier.weight(1f)
                        )

                    }
                }
            }

            when (data.characterData) {
                is ReviewCharacterData.KanaReviewData -> {

                    Text(
                        text = data.characterData.kanaSystem.stringResource(),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = data.characterData.romaji,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )

                }
                is ReviewCharacterData.KanjiReviewData -> {

                    data.characterData.on.takeIf { it.isNotEmpty() }?.let {
                        KanjiReadingRow(
                            titleResId = R.string.writing_practice_reading_on,
                            readings = it
                        )
                    }

                    data.characterData.kun.takeIf { it.isNotEmpty() }?.let {
                        KanjiReadingRow(
                            titleResId = R.string.writing_practice_reading_kun,
                            readings = it
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            charData.run { if (data.isStudyMode || data.isCharacterDrawn) words else encodedWords }
                .takeIf { it.isNotEmpty() }
                ?.let { words ->

                    ExpressionsSection(
                        words = words,
                        isNoTranslationLayout = isNoTranslationLayout,
                        onClick = onExpressionsClick,
                        modifier = Modifier.onHeightFromScreenBottomFound {
                            expressionPreviewTopPositionState.value = it
                        }
                    )

                }

            Spacer(modifier = Modifier.height(extraBottomPaddingState.value))

        }

    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedCharacterSection(
    data: WritingPracticeInfoSectionData,
    toggleRadicalsHighlight: () -> Unit,
    modifier: Modifier = Modifier
) {

    val radicalsTransition = updateTransition(
        targetState = data.characterData to data.shouldHighlightRadicals,
        label = "Radical highlight transition"
    )

    radicalsTransition.AnimatedContent(
        modifier = modifier
            .size(80.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = toggleRadicalsHighlight),
        contentKey = { it.second },
        transitionSpec = { ContentTransform(fadeIn(), fadeOut()) }
    ) { (characterData, shouldHighlight) ->

        when (shouldHighlight) {
            true -> RadicalKanji(
                strokes = characterData.strokes,
                radicals = characterData.radicals,
                modifier = Modifier.fillMaxSize()
            )
            false -> Kanji(
                strokes = characterData.strokes,
                modifier = Modifier.fillMaxSize()
            )
        }

    }

}

@Composable
private fun KanjiMeanings(
    meanings: List<String>,
    modifier: Modifier = Modifier
) {

    Column(modifier) {

        Text(
            text = meanings.first().capitalize(Locale.current),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth()
        )

        if (meanings.size > 1) {
            MostlySingleLineEliminateOverflowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                meanings.drop(1).forEach {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

            }

        }

    }
}

@Composable
private fun KanjiReadingRow(
    @StringRes titleResId: Int,
    readings: List<String>
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        FuriganaText(
            furiganaString = furiganaStringResource(titleResId),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        AutoBreakRow(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .wrapContentSize(Alignment.CenterStart),
            horizontalAlignment = Alignment.Start
        ) {

            readings.forEach {

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
private fun ExpressionsSection(
    words: List<JapaneseWord>,
    isNoTranslationLayout: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, top = 16.dp)
    ) {

        Text(
            text = if (words.size > WritingPracticeScreenContract.WordsLimit) {
                stringResource(R.string.writing_practice_words_limited)
            } else {
                stringResource(R.string.writing_practice_words_template, words.size)
            },
            style = MaterialTheme.typography.titleLarge
        )

        Row(verticalAlignment = Alignment.Bottom) {

            MostlySingleLineEliminateOverflowRow(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 16.dp, top = 4.dp),
                verticalAlignment = Alignment.Bottom
            ) {

                if (isNoTranslationLayout) {
                    words.take(NoTranslationLayoutPreviewWordsLimit).forEach {
                        FuriganaText(
                            furiganaString = it.readings.first(),
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                } else {
                    FuriganaText(
                        furiganaString = words.first().preview(),
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }

            }

            IconButton(
                onClick = onClick,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowDown, null)
            }

        }


    }

}

@Preview
@Composable
private fun KanjiPreview() {
    AppTheme(useDarkTheme = false) {
        Surface {
            WritingPracticeInfoSection(
                state = WritingPracticeInfoSectionData(
                    characterData = ReviewCharacterData.KanjiReviewData(
                        character = PreviewKanji.kanji,
                        strokes = PreviewKanji.strokes,
                        radicals = PreviewKanji.radicals,
                        words = PreviewKanji.randomWords(),
                        encodedWords = PreviewKanji.randomEncodedWords(),
                        on = PreviewKanji.on,
                        kun = PreviewKanji.kun,
                        meanings = PreviewKanji.meanings
                    ),
                    isStudyMode = false,
                    isCharacterDrawn = false,
                    shouldHighlightRadicals = true,
                    isNoTranslationLayout = Locale.current.language == "ja"
                ).run { mutableStateOf(this) },
                expressionPreviewTopPositionState = remember { mutableStateOf(0.dp) }
            )
        }
    }
}

@Preview
@Composable
private fun KanaPreview() {
    AppTheme(useDarkTheme = false) {
        Surface {
            WritingPracticeInfoSection(
                state = WritingPracticeInfoSectionData(
                    characterData = ReviewCharacterData.KanaReviewData(
                        character = PreviewKanji.kanji,
                        strokes = PreviewKanji.strokes,
                        radicals = PreviewKanji.radicals,
                        words = listOf(
                            JapaneseWord(
                                readings = listOf(buildFuriganaString { append("Long long word that takes whole line") }),
                                meanings = listOf("Translation")
                            )
                        ),
                        encodedWords = PreviewKanji.randomEncodedWords(),
                        kanaSystem = CharactersClassification.Kana.Hiragana,
                        romaji = "a"
                    ),
                    isStudyMode = true,
                    isCharacterDrawn = false,
                    shouldHighlightRadicals = false,
                    isNoTranslationLayout = Locale.current.language == "ja"
                ).run { mutableStateOf(this) },
                expressionPreviewTopPositionState = remember { mutableStateOf(0.dp) }
            )
        }
    }
}

@Preview(locale = "ja")
@Composable
private fun KanjiJapPreview() {
    KanjiPreview()
}

@Preview(locale = "ja")
@Composable
private fun KanaJapPreview() {
    KanaPreview()
}

