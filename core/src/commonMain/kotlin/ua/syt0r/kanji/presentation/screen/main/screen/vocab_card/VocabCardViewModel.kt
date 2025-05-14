package ua.syt0r.kanji.presentation.screen.main.screen.vocab_card

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.app_data.AppDataRepository
import ua.syt0r.kanji.core.user_data.database.VocabCardData
import ua.syt0r.kanji.presentation.screen.main.screen.vocab_card.VocabCardScreenContract.ScreenState
import ua.syt0r.kanji.vocab_card_invalid_kana
import ua.syt0r.kanji.vocab_card_invalid_kanji
import ua.syt0r.kanji.vocab_card_invalid_meaning

class VocabCardViewModel(
    viewModelScope: CoroutineScope,
    screenMode: VocabCardScreenMode,
    suggestedVocabCardData: SuggestedVocabCardData,
    appDataRepository: AppDataRepository,
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
                suggestedMeanings = suggestedVocabCardData.alternativeMeanings
                    .toSet()
                    .plus(jmDictWord.senseList.flatMap { it.glossary })
                    .toList()
            } else {
                kanjiOptions = emptyList()
                kanaOptions = emptyList()
                suggestedMeanings = suggestedVocabCardData.alternativeMeanings
            }

            val kanjiEnabled: MutableState<Boolean> = mutableStateOf(
                value = suggestedVocabCardData.kanjiReading != null
            )

            val kanji = mutableStateOf(suggestedVocabCardData.kanjiReading ?: "")
            val kana = mutableStateOf(suggestedVocabCardData.kanaReading ?: "")

            val dictionaryMeaning = jmDictWord?.senseList?.asSequence()
                ?.flatMap { it.glossary }
                ?.firstOrNull()

            val meaningData = VocabCardMeaningData(
                selectedMeaning = mutableStateOf(
                    value = suggestedVocabCardData.meaning
                        ?: suggestedMeanings.firstOrNull()
                        ?: ""
                ),
                dictionaryMeaning = dictionaryMeaning,
                useDictionaryMeaning = mutableStateOf(
                    value = suggestedVocabCardData.useDictionaryMeaningByDefault &&
                            suggestedVocabCardData.meaning == null
                            && dictionaryMeaning != null
                ),
                meaningOptions = suggestedMeanings
            )

            snapshotFlow { meaningData.useDictionaryMeaning.value }
                .onEach { if (it) meaningData.selectedMeaning.value = dictionaryMeaning!! }
                .launchIn(viewModelScope)

            val cardState: State<VocabCardEditState> = derivedStateOf {
                val currentKanjiValue = kanji.value
                val kanji = when {
                    kanjiEnabled.value.not() -> null
                    currentKanjiValue.isEmpty() -> {
                        return@derivedStateOf VocabCardEditState.Invalid(Res.string.vocab_card_invalid_kanji)
                    }

                    else -> currentKanjiValue
                }

                val currentKanaValue = kana.value
                if (currentKanaValue.isEmpty())
                    return@derivedStateOf VocabCardEditState.Invalid(Res.string.vocab_card_invalid_kana)

                val useDictionaryMeaning = meaningData.useDictionaryMeaning.value
                val currentMeaningValue = when {
                    useDictionaryMeaning -> null
                    else -> meaningData.selectedMeaning.value
                }
                if (!useDictionaryMeaning && currentMeaningValue?.isEmpty() == true)
                    return@derivedStateOf VocabCardEditState.Invalid(Res.string.vocab_card_invalid_meaning)

                val cardData = VocabCardData(
                    kanjiReading = kanji,
                    kanaReading = currentKanaValue,
                    meaning = currentMeaningValue,
                    dictionaryId = suggestedVocabCardData.jmDictId
                )
                VocabCardEditState.Valid(
                    cardData = cardData,
                    dictionaryMeaning = dictionaryMeaning
                )
            }

            _state.value = ScreenState.Loaded(
                mode = screenMode,
                kanjiEnabled = kanjiEnabled,
                kanji = kanji,
                kanjiOptions = kanjiOptions,
                kana = kana,
                kanaOptions = kanaOptions,
                meaningData = meaningData,
                jmDictId = suggestedVocabCardData.jmDictId,
                cardState = cardState,
            )
        }
    }

}
