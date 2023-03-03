package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import androidx.compose.runtime.State
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.core.kanji_data.data.JapaneseWord
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeScreenConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSelectedOption
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSummaryItem

interface ReadingPracticeContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun initialize(configuration: PracticeScreenConfiguration.Reading)
        fun select(option: ReadingPracticeSelectedOption)

        fun reportScreenShown(configuration: PracticeScreenConfiguration.Reading)

    }

    data class ReviewProgress(
        val pending: Int,
        val repeat: Int,
        val completed: Int
    )

    sealed interface ScreenState {

        object Loading : ScreenState

        sealed interface Loaded : ScreenState {

            val progress: ReviewProgress

            data class KanaReview(
                override val progress: ReviewProgress,
                val reading: String,
                val classification: CharactersClassification.Kana,
                val character: String,
            ) : Loaded

            data class KanjiReview(
                override val progress: ReviewProgress,
                val character: String,
                val on: List<String>,
                val kun: List<String>,
                val meanings: List<String>,
                val words: List<JapaneseWord>,
                val encodedWords: List<JapaneseWord>
            ) : Loaded

        }

        data class Summary(
            val items: List<ReadingPracticeSummaryItem>
        ) : ScreenState

    }

}