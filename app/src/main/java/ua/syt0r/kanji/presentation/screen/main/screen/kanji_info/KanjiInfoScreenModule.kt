package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.use_case.KanjiInfoLoadDataUseCase

@Module
@InstallIn(ViewModelComponent::class)
abstract class KanjiInfoScreenModule {

    @Binds
    abstract fun loadDataUseCase(
        useCase: KanjiInfoLoadDataUseCase
    ): KanjiInfoScreenContract.LoadDataUseCase

}