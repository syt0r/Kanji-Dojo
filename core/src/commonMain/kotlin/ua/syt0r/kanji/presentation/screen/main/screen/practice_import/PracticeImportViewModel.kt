package ua.syt0r.kanji.presentation.screen.main.screen.practice_import

import androidx.compose.runtime.mutableStateOf
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.PracticeImportScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data.GradeImportPracticeCategory
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data.JlptImportPracticeCategory
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data.KanaImportPracticeCategory
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data.WanikaniImportCategory


class PracticeImportViewModel(
    private val analyticsManager: AnalyticsManager
) : PracticeImportScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    init {
        state.value = ScreenState.Loaded(
            listOf(
                KanaImportPracticeCategory,
                JlptImportPracticeCategory,
                GradeImportPracticeCategory,
                WanikaniImportCategory
            )
        )
    }

    override fun reportScreenShown() {
        analyticsManager.setScreen("import")
    }

}