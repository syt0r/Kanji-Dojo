package ua.syt0r.kanji.presentation.screen.screen.writing_practice_import

import androidx.lifecycle.LiveData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_import.data.PracticeImportItem

interface ImportWritingPracticeScreenContract {

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

