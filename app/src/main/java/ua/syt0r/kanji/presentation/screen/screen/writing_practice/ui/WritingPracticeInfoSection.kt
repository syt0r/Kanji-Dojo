package ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.PhantomRow
import ua.syt0r.kanji.presentation.common.ui.kanji.Kanji
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.ReviewCharacterData

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WritingPracticeInfoSection(
    reviewCharacterData: ReviewCharacterData,
    isStudyMode: Boolean,
    modifier: Modifier = Modifier
) {

    AnimatedContent(
        targetState = reviewCharacterData,
        modifier = modifier,
        transitionSpec = {
            ContentTransform(
                targetContentEnter = slideInHorizontally(tween(600)) + fadeIn(tween(600)),
                initialContentExit = slideOutHorizontally(tween(600)) + fadeOut(tween(600))
            ).using(SizeTransform(clip = false))
        }
    ) { characterData ->

        Column(modifier = Modifier.fillMaxWidth()) {
            when (characterData) {
                is ReviewCharacterData.KanaReviewData -> {
                    KanaInfo(characterData, isStudyMode)
                }
                is ReviewCharacterData.KanjiReviewData -> {
                    KanjiInfo(characterData, isStudyMode)
                }
            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KanaInfo(
    data: ReviewCharacterData.KanaReviewData,
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

            Spacer(modifier = Modifier.size(16.dp))

        }

        Text(
            text = data.kanaSystem,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.weight(1f)
        )

    }


    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = data.romaji,
        color = MaterialTheme.colorScheme.onSecondary,
        modifier = Modifier
            .padding(top = 4.dp, end = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        maxLines = 1
    )

}


@OptIn(ExperimentalMaterial3Api::class)
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
                style = MaterialTheme.typography.displayMedium,
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
        KanjiReadingsRow(readings = data.kun)

    if (data.on.isNotEmpty())
        KanjiReadingsRow(readings = data.on)

}

@Composable
private fun KanjiReadingsRow(
    readings: List<String>
) {

    // TODO add clickable button to view all translations
    PhantomRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        phantomItemsCount = 0
    ) {

        readings.forEach {

            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                maxLines = 1
            )

        }

//        Text(
//            text = "...",
//            color = MaterialTheme.colorScheme.secondary,
//            modifier = Modifier
//                .padding(top = 4.dp, end = 4.dp)
//                .border(
//                    width = 1.dp,
//                    color = MaterialTheme.colorScheme.secondary,
//                    shape = MaterialTheme.shapes.small
//                )
//                .clip(MaterialTheme.shapes.small)
//                .clickable { }
//                .padding(horizontal = 12.dp, vertical = 4.dp),
//            maxLines = 1
//        )

    }

}

@Preview(showBackground = true, group = "kanji")
@Composable
private fun KanjiPreview() {

    AppTheme {
        WritingPracticeInfoSection(
            PreviewKanji.run {
                ReviewCharacterData.KanjiReviewData(
                    kanji,
                    strokes,
                    (0..4).flatMap { on },
                    (0..10).flatMap { kun },
                    meanings
                )
            },
            isStudyMode = true
        )
    }

}

@Preview(showBackground = true, group = "kanji")
@Composable
private fun DarkKanjiPreview() {

    AppTheme(useDarkTheme = true) {
        Surface {
            WritingPracticeInfoSection(
                PreviewKanji.run {
                    ReviewCharacterData.KanjiReviewData(
                        kanji,
                        strokes,
                        (0..4).flatMap { on },
                        (0..10).flatMap { kun },
                        meanings
                    )
                },
                isStudyMode = true
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun KanaPreview() {

    AppTheme {
        WritingPracticeInfoSection(
            PreviewKanji.run {
                ReviewCharacterData.KanaReviewData(
                    kanji,
                    strokes,
                    kanaSystem = "Hiragana",
                    romaji = "A"
                )
            },
            isStudyMode = false
        )
    }

}