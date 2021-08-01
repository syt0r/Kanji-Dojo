package ua.syt0r.kanji.presentation.screen.screen.writing_practice_preview

import androidx.lifecycle.LiveData

interface WritingPracticePreviewScreenContract {

    interface ViewModel {

        val state: LiveData<State>

        fun loadPracticeInfo(practiceId: Long)

    }

    sealed class State {

        object Init : State()
        object Loading : State()

        data class Loaded(
            val practiceId: Long,
            val kanjiList: List<String>
        ) : State()

    }

}
