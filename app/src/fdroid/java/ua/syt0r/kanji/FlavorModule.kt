package ua.syt0r.kanji

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import ua.syt0r.kanji.core.user_data.AndroidUserPreferencesRepository
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.presentation.androidMultiplatformViewModel
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.settings.SettingsScreenContract
import ua.syt0r.kanji.presentation.screen.settings.FdroidSettingsScreenContent
import ua.syt0r.kanji.presentation.screen.settings.FdroidSettingsScreenContract
import ua.syt0r.kanji.presentation.screen.settings.FdroidSettingsViewModel

val flavorModule = module {

    single<UserPreferencesRepository> {
        AndroidUserPreferencesRepository(
            context = androidApplication(),
            defaultAnalyticsEnabled = false,
            defaultAnalyticsSuggestionEnabled = true
        )
    }

    single<SettingsScreenContract.Content> { FdroidSettingsScreenContent }

    factory<FdroidSettingsScreenContract.ViewModel> {
        FdroidSettingsViewModel(
            viewModelScope = it.component1(),
            userPreferencesRepository = get(),
            analyticsManager = get(),
            reminderScheduler = get()
        )
    }

    androidMultiplatformViewModel<FdroidSettingsScreenContract.ViewModel>()

}