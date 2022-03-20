package ua.syt0r.kanji.presentation.screen.screen.kanji_info

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.LiveData
import ua.syt0r.kanji_db_model.model.KanjiClassifications

interface KanjiInfoScreenContract {

    interface ViewModel {
        val state: LiveData<State>
        fun loadKanjiInfo(kanji: String)
    }

    sealed class State {

        data class Loaded(
            val kanji: String,
            val strokes: List<Path>,
            val on: List<String>,
            val kun: List<String>,
            val meanings: List<String>,
            val jlptLevel: KanjiClassifications.JLPT
        ) : State()

        object Init : State()
        object Loading : State()

    }

}