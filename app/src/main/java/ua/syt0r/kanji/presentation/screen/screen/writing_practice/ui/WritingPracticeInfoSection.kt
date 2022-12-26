package ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.R
import ua.syt0r.kanji.core.kanji_data.data.buildFuriganaString
import ua.syt0r.kanji.presentation.common.getString
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.common.ui.PhantomRow
import ua.syt0r.kanji.presentation.common.ui.kanji.Kanji
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.ReviewCharacterData
import ua.syt0r.kanji_dojo.shared.CharactersClassification

data class WritingPracticeInfoSectionData(
    val characterData: ReviewCharacterData,
    val isStudyMode: Boolean,
    val isCharacterDrawn: Boolean
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WritingPracticeInfoSection(
    state: State<WritingPracticeInfoSectionData>,
    modifier: Modifier = Modifier,
    onExpressionsSectionPositioned: (LayoutCoordinates) -> Unit = {},
    onExpressionsClick: () -> Unit = {}
) {
    val transition = updateTransition(
        targetState = state.value,
        label = "Content Change Transition"
    )
    transition.AnimatedContent(
        contentKey = { it.characterData.character to it.isStudyMode },
        modifier = modifier,
        transitionSpec = {
            ContentTransform(
                targetContentEnter = slideInHorizontally(tween(600)) + fadeIn(tween(600)),
                initialContentExit = slideOutHorizontally(tween(600)) + fadeOut(tween(600))
            ).using(SizeTransform(clip = false))
        }
    ) { (characterData, isStudyMode, isCharacterDrawn) ->

        Column(modifier = Modifier.fillMaxWidth()) {
            when (characterData) {
                is ReviewCharacterData.KanaReviewData -> {
                    KanaInfo(characterData, isStudyMode)
                }
                is ReviewCharacterData.KanjiReviewData -> {
                    KanjiInfo(characterData, isStudyMode)
                }
            }

            if (characterData.words.isNotEmpty()) {

                Spacer(modifier = Modifier.height(16.dp))

                ExpressionsPreview(
                    data = characterData,
                    isStudyMode = isStudyMode,
                    isCharacterDrawn = isCharacterDrawn,
                    onClick = onExpressionsClick,
                    modifier = Modifier.onGloballyPositioned(onExpressionsSectionPositioned)
                )

            }

        }

    }

}

@Composable
private fun ColumnScope.KanaInfo(
    data: ReviewCharacterData.KanaReviewData,
    isStudyMode: Boolean
) {

    Row(
        modifier = Modifier.align(CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (isStudyMode) {

            Kanji(
                strokes = data.strokes,
                modifier = Modifier.size(80.dp)
            )

        }

    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier
            .align(CenterHorizontally)
            .wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = data.kanaSystem.getString(),
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = data.romaji.capitalize(Locale.current),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(top = 4.dp, end = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
            maxLines = 1
        )
    }


}

@Composable
private fun KanjiInfo(
    data: ReviewCharacterData.KanjiReviewData,
    isStudyMode: Boolean
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (isStudyMode) {

            Card(
                modifier = Modifier
                    .size(80.dp),
                elevation = CardDefaults.elevatedCardElevation()
            ) {
                Kanji(
                    strokes = data.strokes,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

        }

        Column {

            Text(
                text = data.meanings.first().capitalize(Locale.current),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )

            if (data.meanings.size > 1) {

                // TODO add clickable button to view all translations
                PhantomRow(
                    modifier = Modifier.fillMaxWidth(),
                    phantomItemsCount = 0
                ) {

                    data.meanings.drop(1).forEach {

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

    Spacer(modifier = Modifier.height(24.dp))

    if (data.kun.isNotEmpty())
        KanjiReadingsRow(stringResource(R.string.writing_practice_reading_kun), readings = data.kun)

    if (data.on.isNotEmpty())
        KanjiReadingsRow(stringResource(R.string.writing_practice_reading_on), readings = data.on)

}

@Composable
private fun KanjiReadingsRow(
    title: String,
    readings: List<String>
) {

    // TODO add clickable button to view all translations
    PhantomRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        phantomItemsCount = 0
    ) {

        Text(text = title, style = MaterialTheme.typography.titleSmall)

        Spacer(modifier = Modifier.width(8.dp))

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

@Composable
private fun ExpressionsPreview(
    data: ReviewCharacterData,
    isStudyMode: Boolean,
    isCharacterDrawn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(start = 16.dp)

    ) {

        Text(
            text = stringResource(R.string.writing_practice_words_template, data.words.size),
            modifier = Modifier.padding(top = 10.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )

        Row {

            val previewWord = if (isStudyMode || isCharacterDrawn) data.words.first()
            else data.encodedWords.first()

            FuriganaText(
                furiganaString = buildFuriganaString {
                    append(previewWord.furiganaString)
                    append(" - ")
                    append(previewWord.meanings.first())
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 10.dp)
            )

            IconButton(onClick = onClick) {
                Icon(Icons.Default.KeyboardArrowDown, null)
            }

        }

    }

}


@Preview(showBackground = true, group = "kanji")
@Composable
private fun KanjiPreview() {
    AppTheme {
        WritingPracticeInfoSection(
            state = WritingPracticeInfoSectionPreviewUtils.kanjiPreviewState(
                isStudyMode = true,
                isCharacterDrawn = true
            ),
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(showBackground = true, group = "kanji")
@Composable
private fun DarkKanjiPreview() {
    AppTheme(useDarkTheme = true) {
        Surface {
            WritingPracticeInfoSection(
                state = WritingPracticeInfoSectionPreviewUtils.kanjiPreviewState(
                    isStudyMode = false,
                    isCharacterDrawn = false
                ),
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun KanaPreview() {

    AppTheme {
        WritingPracticeInfoSection(
            state = WritingPracticeInfoSectionPreviewUtils.kanaPreviewState(
                isStudyMode = false,
                isCharacterDrawn = false
            ),
            modifier = Modifier.padding(20.dp)
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun KanaStudyPreview() {

    AppTheme {
        WritingPracticeInfoSection(
            state = WritingPracticeInfoSectionPreviewUtils.kanaPreviewState(
                isStudyMode = true,
                isCharacterDrawn = false
            ),
            modifier = Modifier.padding(20.dp)
        )
    }

}

object WritingPracticeInfoSectionPreviewUtils {

    fun kanjiPreviewState(
        isStudyMode: Boolean,
        isCharacterDrawn: Boolean
    ) = WritingPracticeInfoSectionData(
        characterData = ReviewCharacterData.KanjiReviewData(
            character = PreviewKanji.kanji,
            strokes = PreviewKanji.strokes,
            words = PreviewKanji.randomWords(),
            encodedWords = PreviewKanji.randomWords(),
            on = PreviewKanji.on,
            kun = PreviewKanji.kun,
            meanings = PreviewKanji.meanings
        ),
        isStudyMode = isStudyMode,
        isCharacterDrawn = isCharacterDrawn
    ).run { mutableStateOf(this) }

    fun kanaPreviewState(
        isStudyMode: Boolean,
        isCharacterDrawn: Boolean,
        kanaSystem: CharactersClassification.Kana = CharactersClassification.Kana.HIRAGANA
    ) = WritingPracticeInfoSectionData(
        characterData = ReviewCharacterData.KanaReviewData(
            character = PreviewKanji.kanji,
            strokes = PreviewKanji.strokes,
            words = PreviewKanji.randomWords(),
            encodedWords = PreviewKanji.randomWords(),
            kanaSystem = kanaSystem,
            romaji = "A"
        ),
        isStudyMode = isStudyMode,
        isCharacterDrawn = isCharacterDrawn
    ).run { mutableStateOf(this) }

}
