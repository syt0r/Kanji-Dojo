package ua.syt0r.kanji.ui.screen.screen.writing_practice.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ua.syt0r.kanji.ui.screen.screen.writing_practice.WritingPracticeViewModel

val writingPracticeScreenModule = module {

    viewModel { WritingPracticeViewModel(get()) }

}