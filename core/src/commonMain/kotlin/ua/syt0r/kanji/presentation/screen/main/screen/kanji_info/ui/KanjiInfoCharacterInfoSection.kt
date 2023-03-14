package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.resolveString
import ua.syt0r.kanji.presentation.common.resources.icon.Copy
import ua.syt0r.kanji.presentation.common.resources.icon.ExtraIcons
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.kanji.AnimatedKanji
import ua.syt0r.kanji.presentation.common.ui.kanji.KanjiBackground
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract.ScreenState

@Composable
fun KanjiInfoCharacterInfoSection(
    screenState: ScreenState.Loaded,
    onCopyButtonClick: () -> Unit
) {
    when (screenState) {
        is ScreenState.Loaded.Kana -> {
            KanaInfo(
                screenState = screenState,
                onCopyButtonClick = onCopyButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp)
            )
        }
        is ScreenState.Loaded.Kanji -> {
            KanjiInfo(
                screenState = screenState,
                onCopyButtonClick = onCopyButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun KanaInfo(
    screenState: ScreenState.Loaded.Kana,
    onCopyButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier) {

        Row {

            AnimatableCharacter(screenState.strokes)

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {

                Text(
                    text = screenState.kanaSystem.resolveString(),
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = resolveString { kanjiInfo.romajiMessage(screenState.reading) },
                    style = MaterialTheme.typography.titleSmall
                )

                OutlinedIconButton(
                    onClick = onCopyButtonClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(ExtraIcons.Copy, null)
                }

            }

        }
    }

}

@Composable
private fun KanjiInfo(
    screenState: ScreenState.Loaded.Kanji,
    onCopyButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier) {

        Row {

            AnimatableCharacter(strokes = screenState.strokes)

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {

                screenState.grade?.let {
                    Text(
                        text = resolveString { kanjiInfo.gradeMessage(it) },
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                screenState.jlptLevel?.let {
                    Text(
                        text = resolveString { kanjiInfo.jlptMessage(it) },
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                screenState.frequency?.let {
                    Text(
                        text = resolveString { kanjiInfo.frequencyMessage(it) },
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                OutlinedIconButton(
                    onClick = onCopyButtonClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(ExtraIcons.Copy, null)
                }

            }

        }

        Spacer(modifier = Modifier.size(16.dp))

        Row {

            AutoBreakRow(Modifier.weight(1f)) {

                screenState.meanings.forEach {
                    Text(
                        text = it,
                        modifier = Modifier
                            .alignByBaseline()
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                    )
                }

            }

        }

        Spacer(modifier = Modifier.size(16.dp))

        if (screenState.kun.isNotEmpty())
            ReadingRow(title = resolveString { kunyomi }, items = screenState.kun)

        if (screenState.on.isNotEmpty())
            ReadingRow(title = resolveString { onyomi }, items = screenState.on)

    }

}

@Composable
private fun AnimatableCharacter(strokes: List<Path>) {

    Column {

        Card(
            modifier = Modifier.size(120.dp),
            elevation = CardDefaults.elevatedCardElevation()
        ) {

            Box(modifier = Modifier.fillMaxSize()) {

                KanjiBackground(Modifier.fillMaxSize())

                AnimatedKanji(
                    strokes = strokes,
                    modifier = Modifier.fillMaxSize()
                )

            }

        }

        Text(
            text = resolveString { kanjiInfo.strokesMessage(strokes.size) },
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally)
        )

    }

}

@Composable
private fun ReadingRow(
    title: String,
    items: List<String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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