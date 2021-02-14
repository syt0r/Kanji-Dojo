package ua.syt0r.kanji.ui.screen.screen.home.screen.settings.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ua.syt0r.kanji.ui.screen.screen.home.screen.settings.SettingsViewModel

val settingsScreenModule = module {

    viewModel { SettingsViewModel() }

}