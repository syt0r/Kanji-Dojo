package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import org.koin.dsl.module
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.use_case.LoadWritingPracticeCharacterDataUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.writing_practice.use_case.LoadWritingPracticeDataUseCase

val writingPracticeScreenModule = module {

    factory<WritingPracticeScreenContract.LoadPracticeData> {
        LoadWritingPracticeDataUseCase(
            appStateManager = get(),
            loadCharacterDataUseCase = get()
        )
    }

    factory<WritingPracticeScreenContract.LoadCharacterDataUseCase> {
        LoadWritingPracticeCharacterDataUseCase(
            kanjiRepository = get()
        )
    }

    factory<WritingPracticeScreenContract.ViewModel> {
        WritingPracticeViewModel(
            viewModelScope = it.component1(),
            loadDataUseCase = get(),
            kanjiStrokeEvaluator = get(),
            practiceRepository = get(),
            preferencesRepository = get(),
            analyticsManager = get(),
            timeUtils = get()
        )
    }

    single<WritingPracticeScreenContract.Content> { DefaultWritingPracticeScreenContent }

}