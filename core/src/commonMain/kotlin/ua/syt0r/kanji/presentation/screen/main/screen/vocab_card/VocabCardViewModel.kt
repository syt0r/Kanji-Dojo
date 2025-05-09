package ua.syt0r.kanji.presentation.screen.main.screen.vocab_card

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.user_data.database.VocabCardData
import ua.syt0r.kanji.presentation.screen.main.screen.vocab_card.VocabCardScreenContract.ScreenMode
import ua.syt0r.kanji.presentation.screen.main.screen.vocab_card.VocabCardScreenContract.ScreenState

class VocabCardViewModel(
    viewModelScope: CoroutineScope,
    appDataRepository: AppDataRepository,
    suggestedVocabCardData: SuggestedVocabCardData
) : VocabCardScreenContract.ViewModel {

    private val _state = MutableStateFlow<ScreenState>(value = ScreenState.Loading)
    override val state: StateFlow<ScreenState> = _state

    init {
        viewModelScope.launch {

            val jmDictWord = suggestedVocabCardData.jmDictId
                ?.let { appDataRepository.getDetailedWord(it) }

            val kanjiOptions: List<VocabCardReadingSuggestion>
            val kanaOptions: List<VocabCardReadingSuggestion>
            val suggestedMeanings: List<String>

            if (jmDictWord != null) {
                val readings = jmDictWord.senseList.flatMap { it.readings }
                kanjiOptions = readings
                    .groupBy { it.kanji }
                    .mapNotNull { (kanji, readings) ->
                        VocabCardReadingSuggestion(
                            reading = kanji ?: return@mapNotNull null,
                            matchingReadings = readings.map { it.kana }.distinct()
                        )
                    }
                kanaOptions = readings
                    .groupBy { it.kana }
                    .mapNotNull { (kana, readings) ->
                        VocabCardReadingSuggestion(
                            reading = kana,
                            matchingReadings = readings.mapNotNull { it.kanji }.distinct()
                        )
                    }
                suggestedMeanings = suggestedVocabCardData.suggestedMeanings
                    .toSet()
                    .plus(jmDictWord.senseList.flatMap { it.glossary })
                    .toList()
            } else {
                kanjiOptions = emptyList()
                kanaOptions = emptyList()
                suggestedMeanings = suggestedVocabCardData.suggestedMeanings
            }

            val kanjiEnabled: MutableState<Boolean> = mutableStateOf(
                value = suggestedVocabCardData.kanjiReading != null
            )

            val kanji = mutableStateOf(suggestedVocabCardData.kanjiReading ?: "")
            val kana = mutableStateOf(suggestedVocabCardData.kanaReading)
            val meaning = mutableStateOf(
                value = suggestedVocabCardData.suggestedMeanings.firstOrNull() ?: ""
            )

            val cardState: State<VocabCardEditState> = derivedStateOf {
                val currentKanjiValue = kanji.value
                val kanji = when {
                    kanjiEnabled.value.not() -> null
                    currentKanjiValue.isEmpty() -> {
                        return@derivedStateOf VocabCardEditState.Invalid("No kanji reading")
                    }

                    else -> currentKanjiValue
                }

                val currentKanaValue = kana.value
                if (currentKanaValue.isEmpty())
                    return@derivedStateOf VocabCardEditState.Invalid("No kana reading")

                val currentMeaningValue = meaning.value
                if (currentMeaningValue.isEmpty())
                    return@derivedStateOf VocabCardEditState.Invalid("No meaning")

                val cardData = VocabCardData(
                    kanjiReading = kanji,
                    kanaReading = currentKanaValue,
                    meaning = currentMeaningValue,
                    dictionaryId = suggestedVocabCardData.jmDictId
                )
                VocabCardEditState.Valid(cardData)
            }

            _state.value = ScreenState.Loaded(
                mode = when {
                    suggestedVocabCardData.cardId != null -> ScreenMode.Edit
                    else -> ScreenMode.Save
                },
                kanjiEnabled = kanjiEnabled,
                kanji = kanji,
                kanjiOptions = kanjiOptions,
                kana = kana,
                kanaOptions = kanaOptions,
                meaning = meaning,
                meaningOptions = suggestedMeanings,
                jmDictId = suggestedVocabCardData.jmDictId,
                cardState = cardState,
            )
        }
    }

}
