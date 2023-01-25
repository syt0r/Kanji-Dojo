package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.presentation.common.stringResource
import ua.syt0r.kanji.presentation.common.ui.AutoBreakRow
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.furiganaStringResource
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
                    text = screenState.kanaSystem.stringResource(),
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = stringResource(
                        R.string.kanji_info_kana_romaji_template,
                        screenState.reading
                    ),
                    style = MaterialTheme.typography.titleSmall
                )

                OutlinedIconButton(
                    onClick = onCopyButtonClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(painterResource(R.drawable.ic_baseline_content_copy_24), null)
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
                        text = when {
                            it <= 6 -> stringResource(R.string.kanji_info_joyo_grade_template, it)
                            it == 8 -> stringResource(R.string.kanji_info_joyo_grade_high)
                            it >= 9 -> stringResource(R.string.kanji_info_joyo_grade_names)
                            else -> throw IllegalStateException("Unknown grade $it for kanji ${screenState.character}")
                        },
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                screenState.jlpt?.let {
                    Text(
                        text = stringResource(R.string.kanji_info_jlpt_template, it.toString()),
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                screenState.frequency?.let {
                    Text(
                        text = stringResource(R.string.kanji_info_frequency_template, it),
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                OutlinedIconButton(
                    onClick = onCopyButtonClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(painterResource(R.drawable.ic_baseline_content_copy_24), null)
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
            ReadingRow(titleResId = R.string.kanji_info_kun, items = screenState.kun)

        if (screenState.on.isNotEmpty())
            ReadingRow(titleResId = R.string.kanji_info_on, items = screenState.on)

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
            text = buildAnnotatedString {
                val text = stringResource(R.string.kanji_info_strokes_count, strokes.size)
                append(text)
                val numberStyle = SpanStyle(fontWeight = FontWeight.Bold)
                "\\d+".toRegex().findAll(text).forEach {
                    addStyle(numberStyle, it.range.first, it.range.last + 1)
                }
            },
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally)
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