package ua.syt0r.kanji.presentation.screen.main

import org.koin.dsl.module
import ua.syt0r.kanji.presentation.multiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.features.DeepLinkHandler

val mainScreenModule = module {

    multiplatformViewModel<MainContract.ViewModel> {
        MainScreenViewModel(
            viewModelScope = it.component1(),
            appPreferences = get(),
            migrationObservable = get(),
            syncManager = get()
        )
    }

    single { DeepLinkHandler() }

}