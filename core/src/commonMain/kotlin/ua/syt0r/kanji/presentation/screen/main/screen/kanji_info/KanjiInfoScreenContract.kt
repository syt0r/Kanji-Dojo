package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info

import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Path
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.common.db.entity.CharacterRadical
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord

interface KanjiInfoScreenContract {

    interface ViewModel {
        val state: State<ScreenState>
        fun loadCharacterInfo(character: String)
        fun reportScreenShown(character: String)
    }

    sealed class ScreenState {

        object Loading : ScreenState()
        object NoData : ScreenState()
        sealed class Loaded : ScreenState() {

            abstract val character: String
            abstract val strokes: List<Path>
            abstract val radicals: List<CharacterRadical>
            abstract val words: List<JapaneseWord>

            data class Kana(
                override val character: String,
                override val strokes: List<Path>,
                override val radicals: List<CharacterRadical>,
                override val words: List<JapaneseWord>,
                val kanaSystem: CharactersClassification.Kana,
                val reading: String,
            ) : Loaded()

            data class Kanji(
                override val character: String,
                override val strokes: List<Path>,
                override val radicals: List<CharacterRadical>,
                override val words: List<JapaneseWord>,
                val on: List<String>,
                val kun: List<String>,
                val meanings: List<String>,
                val grade: Int?,
                val jlptLevel: Int?,
                val frequency: Int?,
                val wanikaniLevel: Int?
            ) : Loaded()

        }

    }

    interface LoadDataUseCase {
        fun load(character: String): ScreenState
    }

}