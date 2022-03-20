package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawData
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.DrawResult
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.PracticeConfiguration

interface WritingPracticeScreenContract {

    interface ViewModel {

        val state: LiveData<State>

        fun init(practiceConfiguration: PracticeConfiguration)
        fun submitUserDrawnPath(drawData: DrawData): Flow<DrawResult>

        fun handleCorrectlyDrawnStroke()
        fun handleIncorrectlyDrawnStroke()

    }

    sealed class State {

        object Init : State()
        object Loading : State()

        data class ReviewingKanji(
            val kanji: String,
            val on: List<String>,
            val kun: List<String>,
            val meanings: List<String>,
            val strokes: List<Path>,
            val drawnStrokesCount: Int = 0,
            val mistakes: Int = 0
        ) : State()

        data class Summary(
            val timeSpent: String
        ) : State()

    }

}