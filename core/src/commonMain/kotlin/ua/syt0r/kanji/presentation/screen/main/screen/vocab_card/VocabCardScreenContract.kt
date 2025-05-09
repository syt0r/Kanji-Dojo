package ua.syt0r.kanji.presentation.screen.main.screen.vocab_card

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import ua.syt0r.kanji.core.user_data.database.VocabCardData

interface VocabCardScreenContract {

    interface ViewModel {
        val state: StateFlow<ScreenState>
    }

    sealed interface ScreenState {

        object Loading : ScreenState

        data class Loaded(
            val mode: VocabCardScreenMode,
            val kanjiEnabled: MutableState<Boolean>,
            val kanji: MutableState<String>,
            val kanjiOptions: List<VocabCardReadingSuggestion>,
            val kana: MutableState<String>,
            val kanaOptions: List<VocabCardReadingSuggestion>,
            val meaning: MutableState<String>,
            val meaningOptions: List<String>,
            val jmDictId: Long?,
            val cardState: State<VocabCardEditState>
        ) : ScreenState

    }

    object EditResultStorage {

        var result: VocabCardEditResult? = null
            private set

        fun resetResult() {
            result = null
        }

        fun setResult(result: VocabCardEditResult) {
            this@EditResultStorage.result = result
        }

    }

}

@Serializable
data class SuggestedVocabCardData(
    val kanjiReading: String?,
    val kanaReading: String,
    val suggestedMeanings: List<String>,
    val jmDictId: Long?,
    val cardId: Long?
) {

    constructor(cardId: Long?, cardData: VocabCardData) : this(
        kanjiReading = cardData.kanjiReading,
        kanaReading = cardData.kanaReading,
        suggestedMeanings = listOfNotNull(cardData.meaning),
        jmDictId = cardData.dictionaryId,
        cardId = cardId
    )

}

@Serializable
sealed interface VocabCardScreenMode {

    @Serializable
    object Save : VocabCardScreenMode

    @Serializable
    data class Edit(val index: Int) : VocabCardScreenMode

}

data class VocabCardReadingSuggestion(
    val reading: String,
    val matchingReadings: List<String>
)

sealed interface VocabCardEditState {
    data class Invalid(val message: String) : VocabCardEditState
    data class Valid(val cardData: VocabCardData) : VocabCardEditState
}

data class VocabCardEditResult(
    val index: Int,
    val cardData: VocabCardData
)
