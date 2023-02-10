package ua.syt0r.kanji.presentation.screen.main.screen.practice_preview

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case.*

@Module
@InstallIn(ViewModelComponent::class)
abstract class PracticePreviewScreenModule {

    @Binds
    abstract fun fetchListUseCase(
        useCase: PracticePreviewFetchGroupItemsUseCase
    ): PracticePreviewScreenContract.FetchGroupItemsUseCase

    @Binds
    abstract fun sortListUseCase(
        practicePreviewSortListUseCase: PracticePreviewSortGroupItemsUseCase
    ): PracticePreviewScreenContract.SortGroupItemsUseCase

    @Binds
    abstract fun createGroupsUseCase(
        useCase: CreatePracticeGroupsUseCase
    ): PracticePreviewScreenContract.CreatePracticeGroupsUseCase

    @Binds
    abstract fun characterStateUseCase(
        useCase: PracticePreviewCharacterStateUseCase
    ): PracticePreviewScreenContract.CalculateCharacterStateUseCase

    @Binds
    abstract fun loadScreenData(
        useCase: PracticePreviewLoadScreenStateUseCase
    ): PracticePreviewScreenContract.LoadScreenDataUseCase

}