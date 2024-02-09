package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.resolveString
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.KanaVoiceAutoPlayToggle
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingReviewCharacterData

@Composable
fun ColumnScope.ReadingPracticeCharacterDetailsUI(
    data: ReadingReviewCharacterData,
    showAnswer: State<Boolean>,
    kanaAutoPlay: State<Boolean>,
    toggleKanaAutoPlay: () -> Unit,
    playKanaSound: (romaji: String) -> Unit
) {

    Column(
        modifier = Modifier.align(Alignment.CenterHorizontally)
            .widthIn(max = 400.dp)
            .width(IntrinsicSize.Min)
    ) {

        val alphaState = animateFloatAsState(targetValue = if (showAnswer.value) 1f else 0f)

        when (data) {

            is ReadingReviewCharacterData.Kana -> KanaData(
                data = data,
                alphaState = alphaState,
                kanaAutoPlay = kanaAutoPlay,
                toggleKanaAutoPlay = toggleKanaAutoPlay,
                playKanaSound = playKanaSound
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
    playKanaSound: (romaji: String) -> Unit
) {

    Text(
        text = data.character,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().alpha(alphaState.value)
    ) {

        Text(
            text = data.reading.capitalize(Locale.current),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )

        Text(
            text = data.classification.resolveString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f).padding(start = 6.dp)
        )

    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().alpha(alphaState.value)
    ) {

        IconButton(
            onClick = { playKanaSound(data.reading) },
            enabled = alphaState.value != 0f,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.VolumeUp, null)
        }

        KanaVoiceAutoPlayToggle(
            enabledState = kanaAutoPlay,
            onClick = toggleKanaAutoPlay,
            modifier = Modifier.weight(1f)
        )

    }

}

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


    AutoBreakRow(
        horizontalItemSpacing = 12.dp,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .padding(bottom = 16.dp)
            .alpha(alphaState.value)
    ) {
        data.meanings.forEach { Text(text = it) }
    }

    if (data.kun.isNotEmpty()) {
        KanjiReadingRow(
            title = resolveString { kunyomi },
            items = data.kun,
            modifier = Modifier.alpha(alphaState.value)
        )
    }

    if (data.on.isNotEmpty()) {
        KanjiReadingRow(
            title = resolveString { onyomi },
            items = data.on,
            modifier = Modifier.alpha(alphaState.value)
        )
    }
}


@Composable
private fun KanjiReadingRow(
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
