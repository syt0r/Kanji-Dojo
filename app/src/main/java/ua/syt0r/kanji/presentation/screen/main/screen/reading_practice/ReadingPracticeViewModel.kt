package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.ReadingPracticeContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeConfiguration
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.data.ReadingPracticeSelectedOption
import javax.inject.Inject

class ReadingPracticeViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager
) : ViewModel(), ReadingPracticeContract.ViewModel {

    override val state: State<ScreenState> = mutableStateOf(ScreenState.Loading)

    override fun initialize(configuration: ReadingPracticeConfiguration) {
        TODO("Not yet implemented")
    }

    override fun select(option: ReadingPracticeSelectedOption) {
        TODO("Not yet implemented")
    }

    override fun reportScreenShown(configuration: ReadingPracticeConfiguration) {
        TODO("Not yet implemented")
    }

}