package ua.syt0r.kanji.presentation.screen.screen.practice_import

import androidx.lifecycle.LiveData
import ua.syt0r.kanji.presentation.screen.screen.practice_import.data.PracticeImportItem

interface PracticeImportScreenContract {

    interface ViewModel {
        val state: LiveData<State>
    }

    sealed class State {

        object Loading : State()

        data class Loaded(
            val items: List<PracticeImportItem>
        ) : State()

    }

}

