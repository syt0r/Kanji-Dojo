package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.LiveData

class WritingPracticeScreenContract {

    interface Navigation {

        companion object {
            const val SCREEN_NAME = "review"
        }

        class Callbacks(
            val onAcceptButtonClick: () -> Unit,
            val onRepeatButtonClick: () -> Unit
        )

    }


    interface ViewModel {
        val state: LiveData<State>

        fun init(kanji: String)
        fun submitUserDrawnPath(path: Path, areaSize: Int)
    }

    sealed class State {

        data class DrawingKanji(
            val stokes: List<Path>,
            val drawnStrokesCount: Int
        ) : State()

    }

}