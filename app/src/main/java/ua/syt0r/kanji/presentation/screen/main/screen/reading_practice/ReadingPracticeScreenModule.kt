package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.use_case.*
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.use_case.ReadingPracticeLoadCharactersDataUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.use_case.ReadingPracticeSaveResultsUseCase

@Module
@InstallIn(ViewModelComponent::class)
abstract class ReadingPracticeScreenModule {

    @Binds
    abstract fun loadDataUseCase(
        useCase: ReadingPracticeLoadCharactersDataUseCase
    ): ReadingPracticeContract.LoadCharactersDataUseCase

    @Binds
    abstract fun saveResultsUseCase(
        useCase: ReadingPracticeSaveResultsUseCase
    ): ReadingPracticeContract.SaveResultsUseCase

}