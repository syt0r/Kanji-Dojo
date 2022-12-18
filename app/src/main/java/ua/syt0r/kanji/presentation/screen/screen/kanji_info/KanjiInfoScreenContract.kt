package ua.syt0r.kanji.presentation.screen.screen.kanji_info

import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji_dojo.shared.CharactersClassification

interface KanjiInfoScreenContract {

    interface ViewModel {
        val state: State<ScreenState>
        fun loadCharacterInfo(character: String)
    }

    sealed class ScreenState {

        object Loading : ScreenState()

        sealed class Loaded : ScreenState() {

            abstract val words: List<JapaneseWord>

            data class Kana(
                val character: String,
                val kanaSystem: CharactersClassification.Kana,
                val reading: String,
                val strokes: List<Path>,
                override val words: List<JapaneseWord>
            ) : Loaded()

            data class Kanji(
                val kanji: String,
                val strokes: List<Path>,
                val on: List<String>,
                val kun: List<String>,
                val meanings: List<String>,
                val grade: Int?,
                val jlpt: CharactersClassification.JLPT?,
                val frequency: Int?,
                override val words: List<JapaneseWord>
            ) : Loaded()

        }

    }

    interface LoadDataUseCase {
        fun load(character: String): ScreenState.Loaded
    }

}