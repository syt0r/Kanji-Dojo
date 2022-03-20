package ua.syt0r.kanji.presentation.screen.screen.writing_practice_create

import androidx.lifecycle.LiveData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_create.data.EnteredKanji

interface CreateWritingPracticeScreenContract {

    interface ViewModel {

        val state: LiveData<State>

        fun initialize(initialKanjiList: List<String>)
        fun submitUserInput(input: String)
        fun createSet(title: String)

    }

    enum class StateType {
        Loading,
        Loaded,
        Saving,
        Done
    }

    data class State(
        val data: Set<EnteredKanji>,
        val stateType: StateType
    )

}

