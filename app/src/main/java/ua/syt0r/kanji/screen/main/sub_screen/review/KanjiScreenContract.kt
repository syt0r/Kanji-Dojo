package ua.syt0r.kanji.screen.main.sub_screen.review

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.LiveData

class KanjiScreenContract {

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