package ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ua.syt0r.kanji.ui.screen.screen.home.screen.writing_dashboard.WritingDashboardViewModel

val writingDashboardScreenModule = module {

    viewModel {
        WritingDashboardViewModel(
            kanjiDataStore = get()
        )
    }

}