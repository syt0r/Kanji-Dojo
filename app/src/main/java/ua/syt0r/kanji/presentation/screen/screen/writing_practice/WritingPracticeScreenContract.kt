package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.data.*
import java.util.*

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
            val data: KanjiData,
            val progress: PracticeProgress,
            val drawnStrokesCount: Int = 0,
            val mistakes: Int = 0
        ) : State()

        data class Summary(
            val mistakesMap: SortedMap<String, Int> = sortedMapOf()
        ) : State()

    }

}