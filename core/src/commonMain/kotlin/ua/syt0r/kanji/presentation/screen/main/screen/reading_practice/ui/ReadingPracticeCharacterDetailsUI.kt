package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingReviewCharacterData

@Composable
fun ColumnScope.ReadingPracticeCharacterDetailsUI(
    data: ReadingReviewCharacterData,
    showAnswer: State<Boolean>
) {

    val alpha by animateFloatAsState(targetValue = if (showAnswer.value) 1f else 0f)

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
