package ua.syt0r.kanji.ui.screen.screen.home.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ua.syt0r.kanji.ui.screen.screen.home.HomeViewModel

val homeScreenModule = module {

    viewModel { HomeViewModel() }

}