package ua.syt0r.kanji.presentation.screen.main.screen.vocab_card

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
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
            val meaningData: VocabCardMeaningData,
            val jmDictId: Long?,
            val cardState: State<VocabCardEditState>
        ) : ScreenState

    }

    object VocabCardResultStorage {

        private var result: VocabCardEditResult? = null

        fun resetResult() {
            result = null
        }

        fun consumeResult(): VocabCardEditResult? {
            val currentResult = result
            resetResult()
            return currentResult
        }

        fun setResult(result: VocabCardEditResult) {
            this@VocabCardResultStorage.result = result
        }

    }

}

@Serializable
data class SuggestedVocabCardData(
    val kanjiReading: String? = null,
    val kanaReading: String? = null,
    val meaning: String? = null,
    val alternativeMeanings: List<String> = emptyList(),
    val jmDictId: Long? = null,
    val useDictionaryMeaningByDefault: Boolean = false,
    val cardId: Long? = null
) {

    constructor(
        cardId: Long?,
        cardData: VocabCardData,
        useDictionaryMeaningByDefault: Boolean
    ) : this(
        kanjiReading = cardData.kanjiReading,
        kanaReading = cardData.kanaReading,
        meaning = cardData.meaning,
        alternativeMeanings = listOfNotNull(cardData.meaning),
        jmDictId = cardData.dictionaryId,
        useDictionaryMeaningByDefault = useDictionaryMeaningByDefault,
        cardId = cardId
    )

}

@Serializable
sealed interface VocabCardScreenMode {

    @Serializable
    object Save : VocabCardScreenMode

    @Serializable
    data class Edit(val index: Int) : VocabCardScreenMode

    @Serializable
    object New : VocabCardScreenMode

}

data class VocabCardReadingSuggestion(
    val reading: String,
    val matchingReadings: List<String>
)

data class VocabCardMeaningData(
    val selectedMeaning: MutableState<String>,
    val dictionaryMeaning: String?,
    val useDictionaryMeaning: MutableState<Boolean>,
    val meaningOptions: List<String>
)

sealed interface VocabCardEditState {
    data class Invalid(
        val message: StringResource
    ) : VocabCardEditState

    data class Valid(
        val cardData: VocabCardData,
        val dictionaryMeaning: String?
    ) : VocabCardEditState
}

sealed interface VocabCardEditResult {

    val cardData: VocabCardData
    val dictionaryMeaning: String?

    data class New(
        override val cardData: VocabCardData,
        override val dictionaryMeaning: String?
    ) : VocabCardEditResult

    data class Existing(
        override val cardData: VocabCardData,
        override val dictionaryMeaning: String?,
        val index: Int
    ) : VocabCardEditResult

}
