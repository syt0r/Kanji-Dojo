package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.*
import java.util.*

interface WritingPracticeScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun init(practiceConfiguration: PracticeConfiguration)
        suspend fun submitUserDrawnPath(drawData: DrawData): DrawResult

        fun handleCorrectlyDrawnStroke()
        fun handleIncorrectlyDrawnStroke()

    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class ReviewingKanji(
            val data: KanjiData,
            val progress: PracticeProgress,
            val drawnStrokesCount: Int = 0,
            val mistakes: Int = 0
        ) : ScreenState()

        data class Summary(
            val mistakesMap: SortedMap<String, Int> = sortedMapOf()
        ) : ScreenState()

    }

}