package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.use_case.SearchScreenLoadRadicalsUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.use_case.SearchScreenProcessInputUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.use_case.SearchScreenSearchByRadicalsUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.use_case.SearchScreenUpdateEnabledRadicalsUseCase


@Module
@InstallIn(ViewModelComponent::class)
abstract class SearchScreenModule {

    @Binds
    abstract fun processInputUseCase(
        useCase: SearchScreenProcessInputUseCase
    ): SearchScreenContract.ProcessInputUseCase

    @Binds
    abstract fun loadRadicals(
        useCase: SearchScreenLoadRadicalsUseCase
    ): SearchScreenContract.LoadRadicalsUseCase

    @Binds
    abstract fun searchByRadicals(
        useCase: SearchScreenSearchByRadicalsUseCase
    ): SearchScreenContract.SearchByRadicalsUseCase

    @Binds
    abstract fun updateEnabledRadicals(
        useCase: SearchScreenUpdateEnabledRadicalsUseCase
    ): SearchScreenContract.UpdateEnabledRadicalsUseCase

}