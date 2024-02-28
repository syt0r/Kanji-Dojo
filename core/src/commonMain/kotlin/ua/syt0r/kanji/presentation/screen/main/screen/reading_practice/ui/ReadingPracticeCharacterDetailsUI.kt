package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.japanese.KanaReading
import ua.syt0r.kanji.presentation.common.resolveString
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.KanaVoiceAutoPlayToggle
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingReviewCharacterData

@Composable
fun ColumnScope.ReadingPracticeCharacterDetailsUI(
    data: ReadingReviewCharacterData,
    showAnswer: State<Boolean>,
    kanaAutoPlay: State<Boolean>,
    toggleKanaAutoPlay: () -> Unit,
    speakKana: (KanaReading) -> Unit
) {

    Column(
        modifier = Modifier.align(Alignment.CenterHorizontally).widthIn(max = 400.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        val alphaState = animateFloatAsState(
            targetValue = if (showAnswer.value) 1f else 0f,
            animationSpec = tween(durationMillis = 150, easing = LinearEasing)
        )

        when (data) {

            is ReadingReviewCharacterData.Kana -> KanaData(
                data = data,
                alphaState = alphaState,
                kanaAutoPlay = kanaAutoPlay,
                toggleKanaAutoPlay = toggleKanaAutoPlay,
                speakKana = speakKana
            )

            is ReadingReviewCharacterData.Kanji -> KanjiData(
                data = data,
                alphaState = alphaState
            )

        }

    }

}

@Composable
private fun ColumnScope.KanaData(
    data: ReadingReviewCharacterData.Kana,
    alphaState: State<Float>,
    kanaAutoPlay: State<Boolean>,
    toggleKanaAutoPlay: () -> Unit,
    speakKana: (reading: KanaReading) -> Unit
) {

    Text(
        text = data.character,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    val buttonsEnabled = alphaState.value != 0f

    TextButton(
        modifier = Modifier.align(Alignment.CenterHorizontally).alpha(alphaState.value),
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        onClick = { speakKana(data.reading) }
    ) {

        Text(
            text = buildAnnotatedString {
                append(data.classification.resolveString())
                append(" ")
                withStyle(MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                    append(data.reading.nihonShiki.capitalize(Locale.current))
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(end = 8.dp)
        )

        Icon(Icons.Default.VolumeUp, null)

    }

    data.reading.alternative?.let { alternativeReadings ->
        Text(
            text = resolveString { commonPractice.additionalKanaReadingsNote(alternativeReadings) },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f),
            modifier = Modifier.alpha(alphaState.value)
        )
    }

    KanaVoiceAutoPlayToggle(
        enabledState = kanaAutoPlay,
        enabled = buttonsEnabled,
        onClick = toggleKanaAutoPlay,
        modifier = Modifier.align(Alignment.CenterHorizontally).alpha(alphaState.value)
    )

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.KanjiData(
    data: ReadingReviewCharacterData.Kanji,
    alphaState: State<Float>
) {

    Text(
        text = data.character,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(bottom = 20.dp)
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth()
            .padding(bottom = 16.dp)
            .alpha(alphaState.value)
    ) {
        data.meanings.forEach { Text(text = it) }
    }

    val titleMaxWidthPx = remember { mutableStateOf(0) }

    if (data.kun.isNotEmpty()) {
        KanjiReadingRow(
            title = resolveString { kunyomi },
            titleMaxWidthPx = titleMaxWidthPx,
            items = data.kun,
            modifier = Modifier.fillMaxWidth().alpha(alphaState.value)
        )
    }

    if (data.on.isNotEmpty()) {
        KanjiReadingRow(
            title = resolveString { onyomi },
            titleMaxWidthPx = titleMaxWidthPx,
            items = data.on,
            modifier = Modifier.alpha(alphaState.value)
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KanjiReadingRow(
    title: String,
    titleMaxWidthPx: MutableState<Int>,
    items: List<String>,
    modifier: Modifier
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.widthIn(
                min = with(LocalDensity.current) { titleMaxWidthPx.value.toDp() }
            ),
            onTextLayout = {
                if (it.size.width > titleMaxWidthPx.value) titleMaxWidthPx.value = it.size.width
            }
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
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
