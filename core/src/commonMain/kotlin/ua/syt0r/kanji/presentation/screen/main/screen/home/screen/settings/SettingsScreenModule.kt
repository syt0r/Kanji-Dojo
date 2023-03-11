package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings

import org.koin.dsl.module

val settingsScreenModule = module {

    factory<SettingsScreenContract.ViewModel> {
        SettingsViewModel(
            viewModelScope = it.component1(),
            userPreferencesRepository = get(),
            analyticsManager = get()
        )
    }

}