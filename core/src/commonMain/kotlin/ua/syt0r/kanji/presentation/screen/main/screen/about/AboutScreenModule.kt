package ua.syt0r.kanji.presentation.screen.main.screen.about

import org.koin.dsl.module

val aboutScreenModule = module {

    factory<AboutScreenContract.ViewModel> {
        AboutScreenViewModel(analyticsManager = get())
    }

}