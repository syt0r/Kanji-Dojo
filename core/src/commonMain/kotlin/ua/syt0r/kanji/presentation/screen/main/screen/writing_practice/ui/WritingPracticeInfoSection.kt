package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
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
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.common.resolveString
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.trackItemPosition
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.MostlySingleLineEliminateOverflowRow
import ua.syt0r.kanji.presentation.common.ui.kanji.Kanji
import ua.syt0r.kanji.presentation.common.ui.kanji.RadicalKanji
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewCharacterDetails
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewData

private const val NoTranslationLayoutPreviewWordsLimit = 5

data class WritingPracticeInfoSectionData(
    val characterData: WritingReviewCharacterDetails,
    val autoPlay: State<Boolean>,
    val isStudyMode: Boolean,
    val isCharacterDrawn: Boolean,
    val shouldHighlightRadicals: Boolean,
    val isNoTranslationLayout: Boolean
)

@Composable
fun State<WritingReviewData>.asInfoSectionState(
    noTranslationsLayout: Boolean,
    radicalsHighlight: State<Boolean>,
    autoPlay: State<Boolean>
): State<WritingPracticeInfoSectionData> {
    return remember {
        derivedStateOf {
            val currentState = value
            currentState.run {
                WritingPracticeInfoSectionData(
                    characterData = characterData,
                    isStudyMode = isStudyMode,
                    isCharacterDrawn = drawnStrokesCount.value == characterData.strokes.size,
                    shouldHighlightRadicals = radicalsHighlight.value,
                    isNoTranslationLayout = noTranslationsLayout,
                    autoPlay = autoPlay
                )
            }
        }
    }
}


private const val TransitionAnimationLength = 400
private const val TransitionHalfLength = TransitionAnimationLength / 2
private const val TransitionSlideDistanceRatio = 10

@Composable
fun WritingPracticeInfoSection(
    state: State<WritingPracticeInfoSectionData>,
    modifier: Modifier = Modifier,
    bottomSheetHeight: MutableState<Dp>,
    onExpressionsClick: () -> Unit = {},
    toggleAutoPlay: () -> Unit = {},
    toggleRadicalsHighlight: () -> Unit = {},
    speakRomaji: (String) -> Unit = {},
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

            val enterTransition = slideInHorizontally(
                tween(TransitionHalfLength, TransitionHalfLength, LinearEasing)
            ) { it / TransitionSlideDistanceRatio } +
                    fadeIn(tween(TransitionHalfLength, TransitionHalfLength, LinearEasing))

            val exitTransition = slideOutHorizontally(
                tween(TransitionHalfLength, 0, LinearEasing)
            ) { -it / TransitionSlideDistanceRatio } +
                    fadeOut(tween(TransitionHalfLength, 0, LinearEasing))

            enterTransition togetherWith exitTransition using SizeTransform(clip = false)
        }
    ) { data ->

        val scrollStateResetKey = data.run { characterData.character to isStudyMode }
        val scrollState = remember(scrollStateResetKey) { ScrollState(0) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {

            when (data.characterData) {
                is WritingReviewCharacterDetails.KanaReviewDetails -> {
                    KanaDetails(
                        details = data.characterData,
                        isStudyMode = data.isStudyMode,
                        autoPlay = data.autoPlay,
                        toggleAutoPlay = toggleAutoPlay,
                        speakRomaji = speakRomaji
                    )
                }

                is WritingReviewCharacterDetails.KanjiReviewDetails -> {
                    KanjiDetails(
                        details = data.characterData,
                        isStudyMode = data.isStudyMode,
                        noTranslationsLayout = data.isNoTranslationLayout,
                        shouldHighlightRadicals = rememberUpdatedState(data.shouldHighlightRadicals),
                        toggleRadicalsHighlight = toggleRadicalsHighlight
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val expressions = data.characterData
                .run { if (data.isStudyMode || data.isCharacterDrawn) words else encodedWords }
                .takeIf { it.isNotEmpty() }

            if (expressions != null) {
                ExpressionsSection(
                    words = expressions,
                    isNoTranslationLayout = data.isNoTranslationLayout,
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

@Composable
private fun ColumnScope.KanaDetails(
    details: WritingReviewCharacterDetails.KanaReviewDetails,
    isStudyMode: Boolean,
    autoPlay: State<Boolean>,
    toggleAutoPlay: () -> Unit,
    speakRomaji: (String) -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        if (isStudyMode) {
            AnimatedCharacterSection(
                details = details,
                shouldHighlightRadicals = rememberUpdatedState(false),
                toggleRadicalsHighlight = { },
                modifier = Modifier
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = details.romaji.capitalize(Locale.current),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )

                IconButton(
                    onClick = { speakRomaji(details.romaji) }
                ) {
                    Icon(Icons.Default.VolumeUp, null)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f).wrapContentSize(Alignment.CenterEnd)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { toggleAutoPlay() }
                        .padding(vertical = 6.dp, horizontal = 8.dp)
                ) {

                    Text(
                        text = "Autoplay",
                        style = MaterialTheme.typography.bodySmall
                    )

                    val activatedCircleColor = MaterialTheme.colorScheme.onSurfaceVariant
                    val deactivatedCircleColor = MaterialTheme.colorScheme.surfaceVariant

                    val activatedIconColor = MaterialTheme.colorScheme.surfaceVariant
                    val deactivatedIconColor = MaterialTheme.colorScheme.onSurfaceVariant

                    val circleColor =
                        if (autoPlay.value) activatedCircleColor else deactivatedCircleColor

                    val iconColor =
                        if (autoPlay.value) activatedIconColor else deactivatedIconColor


                    val icon = when (autoPlay.value) {
                        true -> Icons.Default.PlayArrow
                        false -> Icons.Default.Pause
                    }

                    Box(
                        modifier = Modifier.size(16.dp).background(circleColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

            }


            Text(
                text = details.kanaSystem.resolveString(),
                style = MaterialTheme.typography.bodySmall
            )

        }

    }

}

@Composable
private fun ColumnScope.KanjiDetails(
    details: WritingReviewCharacterDetails.KanjiReviewDetails,
    isStudyMode: Boolean,
    noTranslationsLayout: Boolean,
    shouldHighlightRadicals: State<Boolean>,
    toggleRadicalsHighlight: () -> Unit,
) {

    when {
        noTranslationsLayout -> {}
        isStudyMode -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize()
                    .padding(bottom = 16.dp)
            ) {

                AnimatedCharacterSection(
                    details = details,
                    shouldHighlightRadicals = shouldHighlightRadicals,
                    toggleRadicalsHighlight = toggleRadicalsHighlight,
                    modifier = Modifier.padding(end = 16.dp)
                )

                KanjiMeanings(
                    meanings = details.meanings,
                    modifier = Modifier.weight(1f)
                )

            }
        }

        else -> {
            KanjiMeanings(
                meanings = details.meanings,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    details.on.takeIf { it.isNotEmpty() }?.let {
        KanjiReadingRow(
            title = resolveString { onyomi },
            readings = it
        )
    }

    details.kun.takeIf { it.isNotEmpty() }?.let {
        KanjiReadingRow(
            title = resolveString { kunyomi },
            readings = it
        )
    }

}

@Composable
private fun AnimatedCharacterSection(
    details: WritingReviewCharacterDetails,
    shouldHighlightRadicals: State<Boolean>,
    toggleRadicalsHighlight: () -> Unit,
    modifier: Modifier = Modifier
) {

    val radicalsTransition = updateTransition(
        targetState = details to shouldHighlightRadicals.value,
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
