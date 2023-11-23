package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.common.resolveString
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.trackItemPosition
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.MostlySingleLineEliminateOverflowRow
import ua.syt0r.kanji.presentation.common.ui.kanji.Kanji
import ua.syt0r.kanji.presentation.common.ui.kanji.RadicalKanji
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.ReviewCharacterData
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewData

private const val NoTranslationLayoutPreviewWordsLimit = 5

data class WritingPracticeInfoSectionData(
    val characterData: ReviewCharacterData,
    val isStudyMode: Boolean,
    val isCharacterDrawn: Boolean,
    val shouldHighlightRadicals: Boolean,
    val isNoTranslationLayout: Boolean
)

@Composable
fun State<WritingReviewData>.asInfoSectionState(
    noTranslationsLayout: Boolean,
    radicalsHighlight: State<Boolean>
): State<WritingPracticeInfoSectionData> {
    return remember {
        derivedStateOf {
            val currentState = value
            currentState.run {
                WritingPracticeInfoSectionData(
                    characterData = characterData,
                    isStudyMode = isStudyMode,
                    isCharacterDrawn = drawnStrokesCount == characterData.strokes.size,
                    shouldHighlightRadicals = radicalsHighlight.value,
                    isNoTranslationLayout = noTranslationsLayout
                )
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WritingPracticeInfoSection(
    state: State<WritingPracticeInfoSectionData>,
    modifier: Modifier = Modifier,
    bottomSheetHeight: MutableState<Dp>,
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
                        text = data.characterData.kanaSystem.resolveString(),
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
                            title = resolveString { onyomi },
                            readings = it
                        )
                    }

                    data.characterData.kun.takeIf { it.isNotEmpty() }?.let {
                        KanjiReadingRow(
                            title = resolveString { kunyomi },
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
                        modifier = Modifier.trackItemPosition { data ->
                            if (transition.isRunning) return@trackItemPosition
                            bottomSheetHeight.value = data.heightFromScreenBottom
                                .takeIf { it > 200.dp }
                                ?: data.layoutCoordinates
                                    .findRootCoordinates()
                                    .size
                                    .run { height / data.density.density }
                                    .dp
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
    title: String,
    readings: List<String>
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
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
            text = resolveString { writingPractice.headerWordsMessage(words.size) },
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
