package ua.syt0r.kanji.presentation.screen.main.screen.reading_practice

import org.koin.dsl.module
import ua.syt0r.kanji.presentation.screen.main.screen.reading_practice.use_case.ReadingPracticeLoadCharactersDataUseCase

val readingPracticeScreenModule = module {

    factory<ReadingPracticeContract.LoadCharactersDataUseCase> {
        ReadingPracticeLoadCharactersDataUseCase(
            kanjiDataRepository = get()
        )
    }

    factory<ReadingPracticeContract.ViewModel> {
        ReadingPracticeViewModel(
            viewModelScope = it.component1(),
            loadCharactersDataUseCase = get(),
            preferencesRepository = get(),
            practiceRepository = get(),
            analyticsManager = get(),
            timeUtils = get()
        )
    }

}