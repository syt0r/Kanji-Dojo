package ua.syt0r.kanji.presentation.screen.screen.practice_preview

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.use_case.CreatePracticeGroupsUseCase
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.use_case.PracticePreviewFetchListUseCase
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.use_case.PracticePreviewSortListUseCase

@Module
@InstallIn(ViewModelComponent::class)
abstract class PracticePreviewScreenModule {

    @Binds
    abstract fun fetchListUseCase(
        useCase: PracticePreviewFetchListUseCase
    ): PracticePreviewScreenContract.FetchListUseCase

    @Binds
    abstract fun sortListUseCase(
        practicePreviewSortListUseCase: PracticePreviewSortListUseCase
    ): PracticePreviewScreenContract.SortListUseCase

    @Binds
    abstract fun createGroupsUseCase(
        useCase: CreatePracticeGroupsUseCase
    ): PracticePreviewScreenContract.CreatePracticeGroupsUseCase

}