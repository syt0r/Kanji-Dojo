package ua.syt0r.kanji.presentation.screen.main.screen.practice_import

import org.koin.dsl.module

val practiceImportScreenModule = module {

    factory<PracticeImportScreenContract.ViewModel> {
        PracticeImportViewModel(analyticsManager = get())
    }

}