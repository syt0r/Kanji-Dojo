package ua.syt0r.kanji.presentation.dialog

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.app_data.data.JapaneseWord
import ua.syt0r.kanji.core.app_data.data.formattedVocabStringReading
import ua.syt0r.kanji.core.user_data.database.VocabCardData
import ua.syt0r.kanji.core.user_data.database.VocabPracticeRepository
import ua.syt0r.kanji.presentation.common.AppListItem
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.theme.extraColorScheme
import ua.syt0r.kanji.presentation.common.ui.FilledTextField
import ua.syt0r.kanji.save_word_dialog_button_add
import ua.syt0r.kanji.save_word_dialog_button_cancel
import ua.syt0r.kanji.save_word_dialog_completed_state_message
import ua.syt0r.kanji.save_word_dialog_contains_hint
import ua.syt0r.kanji.save_word_dialog_create_deck_button
import ua.syt0r.kanji.save_word_dialog_create_deck_title_hint
import ua.syt0r.kanji.save_word_dialog_saving_state_message
import ua.syt0r.kanji.save_word_dialog_title


private fun JapaneseWord.toCardData() = VocabCardData(
    kanjiReading = reading.kanjiReading,
    kanaReading = reading.kanaReading,
    meaning = combinedGlossary(),
    dictionaryId = id
)

@Composable
fun SaveWordDialog(
    word: JapaneseWord,
    onDismissRequest: () -> Unit
) {
    SaveWordDialog(
        cardData = word.toCardData(),
        onDismissRequest = onDismissRequest
    )
}

@Composable
fun SaveWordDialog(
    cardData: VocabCardData,
    onDismissRequest: () -> Unit
) {

    val dialogState = rememberDialogState(cardData)

    MultiplatformDialog(
        onDismissRequest = onDismissRequest,
        title = {
            val label = cardData.run { formattedVocabStringReading(kanaReading, kanjiReading) }
            Text(
                text = stringResource(Res.string.save_word_dialog_title, label)
            )
        },
        paddedContent = false,
        content = {
            AnimatedContent(
                targetState = dialogState.state.value,
                modifier = Modifier.fillMaxWidth()
            ) {
                DialogContent(
                    state = it,
                    onDismissRequest = onDismissRequest,
                    createNewDeck = { dialogState.createNewDeck() }
                )
            }
        },
        buttons = {
            TextButton(onDismissRequest) {
                Text(
                    text = stringResource(Res.string.save_word_dialog_button_cancel)
                )
            }
            val isAddButtonEnabled = remember {
                derivedStateOf {
                    dialogState.state.value.let {
                        (it is AddingState.SelectingDeck && it.selectedDeck.value != null) ||
                                (it is AddingState.CreateNewDeck && it.title.value.isNotEmpty())
                    }
                }
            }
            TextButton(
                onClick = { dialogState.save() },
                enabled = isAddButtonEnabled.value
            ) {
                Text(
                    text = stringResource(Res.string.save_word_dialog_button_add)
                )
            }
        }
    )

}

@Composable
private fun DialogContent(
    state: AddingState,
    onDismissRequest: () -> Unit,
    createNewDeck: () -> Unit
) {
    when (state) {
        AddingState.Loading -> {
            CircularProgressIndicator(Modifier.fillMaxWidth().wrapContentWidth())
        }

        is AddingState.SelectingDeck -> {
            Column {
                AppListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.save_word_dialog_create_deck_button))
                    },
                    trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) },
                    onClick = createNewDeck,
                    modifier = Modifier.fillMaxWidth(),
                )
                state.decks.forEach { deck ->
                    AppListItem(
                        headlineContent = { Text(deck.title) },
                        supportingContent = if (deck.alreadyContains) {
                            { Text(stringResource(Res.string.save_word_dialog_contains_hint)) }
                        } else {
                            null
                        },
                        trailingContent = {
                            if (deck.id == state.selectedDeck.value)
                                Icon(Icons.Default.Check, null)
                        },
                        onClick = { state.selectedDeck.value = deck.id },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        is AddingState.CreateNewDeck -> {
            AppListItem(
                headlineContent = {
                    FilledTextField(
                        value = state.title.value,
                        onValueChange = { state.title.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        hintContent = {
                            Text(
                                text = stringResource(Res.string.save_word_dialog_create_deck_title_hint),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            )
        }

        AddingState.Saving -> {
            Text(
                text = stringResource(Res.string.save_word_dialog_saving_state_message),
                modifier = Modifier.fillMaxWidth().wrapContentWidth()
            )
        }

        AddingState.Completed -> {
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.save_word_dialog_completed_state_message)
                )
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .background(MaterialTheme.extraColorScheme.success, CircleShape)
                        .size(24.dp)
                        .padding(2.dp)
                )
            }
            LaunchedEffect(Unit) {
                delay(600)
                onDismissRequest()
            }
        }
    }
}

private sealed interface AddingState {
    data object Loading : AddingState

    data class SelectingDeck(
        val decks: List<AddingDeckInfo>,
        val selectedDeck: MutableState<Long?>
    ) : AddingState

    data class CreateNewDeck(
        val title: MutableState<String>
    ) : AddingState

    data object Saving : AddingState
    data object Completed : AddingState
}

private data class AddingDeckInfo(
    val id: Long,
    val title: String,
    val alreadyContains: Boolean
)

@Composable
private fun rememberDialogState(vocabCardData: VocabCardData): SaveWordDialogState {
    val repository = koinInject<VocabPracticeRepository>()
    val coroutineScope = rememberCoroutineScope()
    return remember {
        SaveWordDialogState(
            vocabCardData = vocabCardData,
            repository = repository,
            coroutineScope = coroutineScope
        )
    }
}

private class SaveWordDialogState(
    private val vocabCardData: VocabCardData,
    private val repository: VocabPracticeRepository,
    private val coroutineScope: CoroutineScope,
) {

    private val _state = mutableStateOf<AddingState>(AddingState.Loading)
    val state: State<AddingState> = _state

    init {
        coroutineScope.launch {
            val decksWithWord = repository
                .getDecksContainingWord(vocabCardData.kanjiReading, vocabCardData.kanaReading)
                .toSet()
            _state.value = AddingState.SelectingDeck(
                decks = repository.getDecks().map {
                    AddingDeckInfo(
                        id = it.id,
                        title = it.title,
                        alreadyContains = decksWithWord.contains(it.id)
                    )
                },
                selectedDeck = mutableStateOf(null)
            )
        }
    }

    fun createNewDeck() {
        _state.value = AddingState.CreateNewDeck(
            title = mutableStateOf("")
        )
    }

    fun save() {
        val currentState = _state.value
        coroutineScope.launch {
            when (currentState) {
                is AddingState.CreateNewDeck -> {
                    _state.value = AddingState.Saving
                    repository.createDeck(
                        title = currentState.title.value,
                        words = listOf(vocabCardData)
                    )
                }

                is AddingState.SelectingDeck -> {
                    val deckId = currentState.selectedDeck.value ?: return@launch
                    _state.value = AddingState.Saving
                    repository.addCard(deckId, vocabCardData)
                }

                else -> return@launch
            }
            _state.value = AddingState.Completed
        }
    }

}
