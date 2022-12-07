package ua.syt0r.kanji.presentation.screen.screen.writing_practice

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.use_case.IsEligibleForInAppReviewUseCase
import ua.syt0r.kanji.presentation.screen.screen.writing_practice.use_case.LoadWritingPracticeDataUseCase

@Module
@InstallIn(ViewModelComponent::class)
abstract class WritingPracticeScreenModule {

    @Binds
    abstract fun fetchDataUseCase(
        useCase: LoadWritingPracticeDataUseCase
    ): WritingPracticeScreenContract.LoadWritingPracticeDataUseCase

    @Binds
    abstract fun eligibilityUseCase(
        useCase: IsEligibleForInAppReviewUseCase
    ): WritingPracticeScreenContract.IsEligibleForInAppReviewUseCase

}