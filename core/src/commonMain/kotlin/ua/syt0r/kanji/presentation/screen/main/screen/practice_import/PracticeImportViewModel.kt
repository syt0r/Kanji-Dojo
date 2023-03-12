package ua.syt0r.kanji.presentation.screen.main.screen.practice_import

import androidx.compose.runtime.mutableStateOf
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.PracticeImportScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_import.data.ImportPracticeCategory


class PracticeImportViewModel(
    private val analyticsManager: AnalyticsManager
) : PracticeImportScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    init {
        state.value = ScreenState.Loaded(ImportPracticeCategory.all)
    }

    override fun reportScreenShown() {
        analyticsManager.setScreen("import")
    }

}