package ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.common.JapaneseWordUI
import ua.syt0r.kanji.presentation.common.Paginateable
import ua.syt0r.kanji.presentation.common.PaginationLoadLaunchedEffect
import ua.syt0r.kanji.presentation.common.collectAsState
import ua.syt0r.kanji.presentation.dialog.SaveWordDialog
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.CharacterWriterConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.practice_common.CharacterWritingProgress
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.LetterPracticeScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeExampleWord
import ua.syt0r.kanji.presentation.screen.main.screen.practice_letter.data.LetterPracticeReviewState


data class BottomSheetStateData(
    val letter: String,
    val reveal: Boolean,
    val examples: Paginateable<LetterPracticeExampleWord>
)

@Composable
fun State<LetterPracticeReviewState.Writing>.asWordsBottomSheetState(): State<BottomSheetStateData> {
    return remember {
        derivedStateOf {
            val currentState = value
            val writerState = currentState.writerState.value
            val isStudyMode = writerState.configuration
                .run { this is CharacterWriterConfiguration.StrokeInput && isStudyMode }
            val revealCharacter = writerState.progress
                .value !is CharacterWritingProgress.Writing

            BottomSheetStateData(
                letter = writerState.character,
                reveal = isStudyMode || revealCharacter,
                examples = currentState.itemData.examples
            )
        }
    }
}

private val DefaultHeight = 300.dp
private val MinHeightThreshold = 200.dp

@Composable
fun letterPracticeWritingWordsBottomSheetHeight(
    scaffoldCoordinates: SharedFlow<LayoutCoordinates?>,
    expressionSectionCoordinates: SharedFlow<LayoutCoordinates?>
): State<Dp> {
    val height = remember { mutableStateOf(DefaultHeight) }
    val density = LocalDensity.current
    val extraBottomSheetHeight = WindowInsets.navigationBars
        .getBottom(density)
        .let { with(density) { it.toDp() } }

    LaunchedEffect(Unit) {
        val scaffoldBoundFlow = scaffoldCoordinates
            .map { it?.takeIf { it.isAttached }?.boundsInWindow() }
        val expressionSectionBoundsFlow = expressionSectionCoordinates
            .map { it?.takeIf { it.isAttached }?.boundsInWindow() }

        scaffoldBoundFlow.combine(expressionSectionBoundsFlow) { a, b -> a to b }
            .map { (scaffold, expressions) ->
                Logger.d("scaffold[$scaffold] expressions[$expressions]")
                when {
                    scaffold == null || expressions == null -> DefaultHeight
                    else -> {
                        val maxHeight = with(density) {
                            scaffold.height.toDp().plus(extraBottomSheetHeight)
                        }

                        val bottomSheetHeight = scaffold.bottom
                            .minus(expressions.top)
                            .let { with(density) { it.toDp() } }
                            .plus(extraBottomSheetHeight)
                            .takeIf { it >= MinHeightThreshold }
                            ?: maxHeight

                        bottomSheetHeight
                    }
                }
            }
            .onEach { vocabSheetHeight ->
                Logger.d("changing bottom sheet height to ${vocabSheetHeight.value}")
                height.value = vocabSheetHeight
            }
            .launchIn(this)
    }
    return height
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterPracticeWritingWordsBottomSheet(
    state: State<BottomSheetStateData>,
    sheetContentHeight: State<Dp>,
    hideSheet: () -> Unit,
    onWordClick: (JapaneseWord) -> Unit
) {

    var wordToAddToVocabDeck by remember { mutableStateOf<JapaneseWord?>(null) }
    wordToAddToVocabDeck?.let {
        SaveWordDialog(
            word = it,
            onDismissRequest = { wordToAddToVocabDeck = null }
        )
    }

    val windowBottomExtraPaddingDp = WindowInsets.safeContent
        .asPaddingValues()
        .calculateBottomPadding()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = sheetContentHeight.value - windowBottomExtraPaddingDp)
            .windowInsetsPadding(BottomSheetDefaults.windowInsets)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            BottomSheetDefaults.DragHandle()
            IconButton(
                onClick = hideSheet,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        val currentState by state

        val listState = rememberSaveable(
            key = currentState.letter,
            saver = LazyListState.Saver
        ) { LazyListState(0) }

        val examples = currentState.examples.collectAsState()

        PaginationLoadLaunchedEffect(
            listState = listState,
            prefetchDistance = LetterPracticeScreenContract.EXAMPLES_PRELOAD_DISTANCE,
            paginateableState = examples
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState
        ) {

            itemsIndexed(examples.list) { index, word ->
                WordExample(
                    index = index,
                    example = word,
                    currentState = currentState,
                    onWordClick = onWordClick,
                    onAddButtonClick = { wordToAddToVocabDeck = word.word }
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

        }

    }

}

@Composable
private fun WordExample(
    index: Int,
    example: LetterPracticeExampleWord,
    currentState: BottomSheetStateData,
    onWordClick: (JapaneseWord) -> Unit,
    onAddButtonClick: (JapaneseWord) -> Unit
) {
    JapaneseWordUI(
        index = index,
        word = example.word,
        headline = {
            WritingPracticeVocabHeadline(
                word = example,
                reveal = currentState.reveal,
                letter = currentState.letter
            )
        },
        onClick = { onWordClick(example.word) }
            .takeIf { currentState.reveal },
        addWordToVocabDeckClick = { onAddButtonClick(example.word) }
            .takeIf { currentState.reveal }
    )
}