package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import org.koin.dsl.module
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.use_case.IsEligibleForInAppReviewUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.use_case.LoadWritingPracticeDataUseCase

val writingPracticeScreenModule = module {

    factory<WritingPracticeScreenContract.LoadWritingPracticeDataUseCase> {
        LoadWritingPracticeDataUseCase(
            kanjiRepository = get()
        )
    }

    factory<WritingPracticeScreenContract.IsEligibleForInAppReviewUseCase> {
        IsEligibleForInAppReviewUseCase(
            practiceRepository = get()
        )
    }

    factory<WritingPracticeScreenContract.ViewModel> {
        WritingPracticeViewModel(
            viewModelScope = it.component1(),
            loadDataUseCase = get(),
            isEligibleForInAppReviewUseCase = get(),
            kanjiStrokeEvaluator = get(),
            practiceRepository = get(),
            preferencesRepository = get(),
            analyticsManager = get()
        )
    }

    single<WritingPracticeScreenContract.Content> { DefaultWritingPracticeScreenContent }

}