package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.use_case.SearchScreenProcessInputUseCase


@Module
@InstallIn(ViewModelComponent::class)
abstract class SearchScreenModule {

    @Binds
    abstract fun processInputUseCase(
        useCase: SearchScreenProcessInputUseCase
    ): SearchScreenContract.ProcessInputUseCase

}