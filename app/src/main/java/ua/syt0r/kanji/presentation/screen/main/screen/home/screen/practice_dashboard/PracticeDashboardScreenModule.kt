package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.practice_dashboard.use_case.PracticeDashboardLoadDataUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case.*

@Module
@InstallIn(ViewModelComponent::class)
abstract class PracticeDashboardScreenModule {

    @Binds
    abstract fun loadDataUseCase(
        useCase: PracticeDashboardLoadDataUseCase
    ): PracticeDashboardScreenContract.LoadDataUseCase

}