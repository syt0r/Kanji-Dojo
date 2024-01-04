package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.common.jsonSaver
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.ui.FuriganaText
import ua.syt0r.kanji.presentation.dialog.AlternativeWordsDialog
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.WritingPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.data.WritingReviewData


data class BottomSheetStateData(
    val reviewCount: Int,
    val words: List<JapaneseWord>
)

@Composable
fun State<WritingReviewData>.asWordsBottomSheetState(): State<BottomSheetStateData> {
    return remember {
        derivedStateOf(referentialEqualityPolicy()) {
            val reviewData = value
            val words = reviewData.run {
                if (isStudyMode || drawnStrokesCount == characterData.strokes.size) characterData.words
                else characterData.encodedWords
            }
            val limitedWords = words.take(WritingPracticeScreenContract.WordsLimit)
            BottomSheetStateData(
                reviewCount = reviewData.progress.totalReviews,
                words = limitedWords
            )
        }
    }
}

@Composable
fun WritingPracticeWordsBottomSheet(
    state: State<BottomSheetStateData>,
    sheetContentHeight: State<Dp>
) {

    var selectedWordForAlternativeDialog by rememberSaveable(stateSaver = jsonSaver()) {
        mutableStateOf<JapaneseWord?>(null)
    }

    selectedWordForAlternativeDialog?.let {
        AlternativeWordsDialog(
            word = it,
            onDismissRequest = { selectedWordForAlternativeDialog = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(sheetContentHeight.value)
    ) {

        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outline)
                .size(width = 60.dp, height = 4.dp)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = resolveString { writingPractice.wordsBottomSheetTitle },
            modifier = Modifier
                .padding(top = 8.dp, bottom = 16.dp)
                .padding(horizontal = 20.dp),
            style = MaterialTheme.typography.titleLarge
        )


        val currentState = state.value

        val listState = remember(currentState.reviewCount) {
            LazyListState(0)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState
        ) {

            itemsIndexed(currentState.words) { index, word ->
                FuriganaText(
                    furiganaString = word.orderedPreview(index),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .heightIn(min = 50.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(onClick = { selectedWordForAlternativeDialog = word })
                        .padding(horizontal = 10.dp)
                        .wrapContentSize(Alignment.CenterStart)
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

        }

    }

}