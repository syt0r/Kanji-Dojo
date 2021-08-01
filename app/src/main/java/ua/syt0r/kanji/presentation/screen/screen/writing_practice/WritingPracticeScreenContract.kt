package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.LiveData

interface WritingPracticeScreenContract {

    interface ViewModel {
        val state: LiveData<State>

        fun init(practiceId: Long)
        fun submitUserDrawnPath(path: Path, areaSize: Int)
    }

    sealed class State {

        object Init : State()

        data class ReviewingKanji(
            val kanji: String,
            val on: List<String>,
            val kun: List<String>,
            val meanings: List<String>,
            val stokes: List<Path>,
            val drawnStrokesCount: Int
        ) : State()

        data class Summary(
            val timeSpent: String
        ) : State()

    }

}