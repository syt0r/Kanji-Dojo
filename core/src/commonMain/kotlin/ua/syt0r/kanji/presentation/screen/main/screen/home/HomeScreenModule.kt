package ua.syt0r.kanji.presentation.screen.main.screen.home

import org.koin.dsl.module

val homeScreenModule = module {

    factory<HomeScreenContract.ViewModel> { parametersHolder ->
        HomeViewModel(parametersHolder.component1())
    }

}