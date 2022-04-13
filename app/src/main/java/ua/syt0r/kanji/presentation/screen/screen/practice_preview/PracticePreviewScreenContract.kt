package ua.syt0r.kanji.presentation.screen.screen.practice_preview

import androidx.lifecycle.LiveData

interface PracticePreviewScreenContract {

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
