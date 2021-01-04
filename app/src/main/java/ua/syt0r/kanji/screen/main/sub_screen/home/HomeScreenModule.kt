package ua.syt0r.kanji.screen.main.sub_screen.home

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ua.syt0r.kanji.screen.main.sub_screen.review.KanjiScreenViewModel

val homeScreenModule = module {

    viewModel { KanjiScreenViewModel(androidApplication(), get()) }

}