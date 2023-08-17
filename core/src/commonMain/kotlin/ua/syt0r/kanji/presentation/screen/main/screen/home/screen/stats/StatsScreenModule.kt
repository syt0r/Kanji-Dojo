package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.stats

import org.koin.dsl.module

val statsScreenModule = module {

    factory<StatsScreenContract.ViewModel> {
        StatsViewModel(
            viewModelScope = it.component1()
        )
    }

}