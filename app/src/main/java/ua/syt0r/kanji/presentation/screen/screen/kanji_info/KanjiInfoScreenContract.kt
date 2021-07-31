package ua.syt0r.kanji.presentation.screen.screen.kanji_info

import androidx.lifecycle.LiveData

interface KanjiInfoScreenContract {

    interface ViewModel {
        val state: LiveData<State>
        fun loadKanjiInfo(kanji: String)
    }

    sealed class State {

        data class Loaded(
            val kanji: String,
            val strokes: List<String>,
            val meanings: List<String>
        ) : State()

        object Init : State()
        object Loading : State()

    }

}