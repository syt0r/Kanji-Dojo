package ua.syt0r.kanji.presentation.screen.screen.writing_practice.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.presentation.common.ui.PhantomRow
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.KanjiData

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WritingPracticeKanjiInfoSection(kanjiData: KanjiData, modifier: Modifier = Modifier) {

    AnimatedContent(
        targetState = kanjiData,
        modifier = modifier.padding(24.dp),
        transitionSpec = {
            ContentTransform(
                targetContentEnter = slideInHorizontally(tween(600)) + fadeIn(tween(600)),
                initialContentExit = slideOutHorizontally(tween(600)) + fadeOut(tween(600))
            ).using(SizeTransform(clip = false))
        }
    ) { kanjiData ->

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = kanjiData.meanings.first().capitalize(Locale.current),
                style = MaterialTheme.typography.displayMedium,
            )

            if (kanjiData.meanings.size > 1) {

                PhantomRow(modifier = Modifier.fillMaxWidth(), phantomItemsCount = 1) {

                    kanjiData.meanings.drop(1).forEach {

                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                    }

                    Text(text = "MORE BUTTON")

                }

            }

            Spacer(modifier = Modifier.height(24.dp))

            if (kanjiData.kun.isNotEmpty())
                KanjiReadingsRow(dataList = kanjiData.kun)

            if (kanjiData.on.isNotEmpty())
                KanjiReadingsRow(dataList = kanjiData.on)

        }

    }

}

@Composable
private fun KanjiReadingsRow(dataList: List<String>) {

    Row {

        PhantomRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {

            dataList.forEach {

                Text(
                    text = it,
                    modifier = Modifier
                        .padding(top = 4.dp, end = 4.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    maxLines = 1
                )

            }

        }
    }

}