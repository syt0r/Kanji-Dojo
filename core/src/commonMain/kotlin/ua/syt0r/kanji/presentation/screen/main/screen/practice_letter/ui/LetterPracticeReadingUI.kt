package ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.app_data.data.formattedVocabDefinition
import ua.syt0r.kanji.core.japanese.KanaReading
import ua.syt0r.kanji.presentation.common.FuriganaWordHeadline
import ua.syt0r.kanji.presentation.common.JapaneseWordUI
import ua.syt0r.kanji.presentation.common.PaginateableState
import ua.syt0r.kanji.presentation.common.PaginationLoadLaunchedEffect
import ua.syt0r.kanji.presentation.common.collectAsState
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.theme.Dimens
import ua.syt0r.kanji.presentation.common.ui.LocalOrientation
import ua.syt0r.kanji.presentation.common.ui.Orientation
import ua.syt0r.kanji.presentation.common.ui.TinyCircularProgressBar
import ua.syt0r.kanji.presentation.dialog.SaveWordDialog
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.FlashcardPracticeAnswerButtonsRow
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.PracticeAnswer
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.LetterPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeExampleWord
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeReviewState

@Composable
fun LetterPracticeReadingUI(
    reviewState: LetterPracticeReviewState.Reading,
    onNextClick: (PracticeAnswer) -> Unit,
    speakKana: (KanaReading) -> Unit,
    onWordClick: (JapaneseWord) -> Unit
) {

    var wordToAddToVocabDeck by remember { mutableStateOf<JapaneseWord?>(null) }
    wordToAddToVocabDeck?.let {
        SaveWordDialog(
            word = it,
            onDismissRequest = { wordToAddToVocabDeck = null }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val listState = rememberSaveable(
            reviewState.itemData.character,
            saver = LazyListState.Saver
        ) { LazyListState(0) }

        val examples = reviewState.itemData.examples.collectAsState()

        PaginationLoadLaunchedEffect(
            listState = listState,
            prefetchDistance = LetterPracticeScreenContract.EXAMPLES_PRELOAD_DISTANCE
        ) { examples.loadMore() }

        if (LocalOrientation.current == Orientation.Portrait) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState
            ) {
                item {
                    LetterPracticeReadingInfoSection(
                        state = reviewState,
                        speakKana = speakKana,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    )
                }
                addWordItems(
                    examples = examples,
                    revealed = reviewState.revealed,
                    onWordClick = onWordClick,
                    addWordToDeck = { wordToAddToVocabDeck = it }
                )
            }
        } else {

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {

                LetterPracticeReadingInfoSection(
                    state = reviewState,
                    speakKana = speakKana,
                    modifier = Modifier.weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    state = listState
                ) {
                    addWordItems(
                        examples = examples,
                        revealed = reviewState.revealed,
                        onWordClick = onWordClick
                    ) { wordToAddToVocabDeck = it }
                }

            }

        }

        FlashcardPracticeAnswerButtonsRow(
            answers = reviewState.answers,
            showAnswer = reviewState.revealed,
            onRevealAnswerClick = { reviewState.revealed.value = true },
            onAnswerClick = onNextClick,
        )

    }


}


@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.addWordItems(
    examples: PaginateableState<LetterPracticeExampleWord>,
    revealed: MutableState<Boolean>,
    onWordClick: (JapaneseWord) -> Unit,
    addWordToDeck: (JapaneseWord) -> Unit
) {

    stickyHeader {

        Text(
            text = resolveString { letterPractice.headerWordsMessage(examples.total) },
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        )

    }

    itemsIndexed(examples.list) { index, word ->
        JapaneseWordUI(
            index = index,
            headline = { VocabExampleHeader(word, revealed.value) },
            onClick = { onWordClick(word.word) }.takeIf { revealed.value },
            addWordToVocabDeckClick = { addWordToDeck(word.word) }.takeIf { revealed.value }
        )
    }

    if (examples.canLoadMore) item {
        TinyCircularProgressBar(
            strokeWidth = Dimens.SpacingTiny,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .padding(vertical = Dimens.SpacingMid)
                .size(Dimens.IconSmall)
        )
    }

    item { Spacer(modifier = Modifier.height(20.dp)) }

}

@Composable
private fun VocabExampleHeader(
    word: LetterPracticeExampleWord,
    revealed: Boolean
) {
    when {
        revealed && word.romaji != null -> {
            Text(
                text = formattedVocabDefinition(word.romaji, word.word.combinedGlossary())
            )
        }

        revealed && word.romaji == null -> {
            FuriganaWordHeadline(
                reading = word.word.reading,
                glossary = word.word.combinedGlossary()
            )
        }

        word.romaji != null -> {
            Text(word.romaji)
        }

        else -> {
            val reading = word.word.reading.run { kanjiReading ?: kanaReading }
            Text(reading)
        }
    }
}
