package ua.syt0r.kanji.presentation.screen.main.screen.practice_create

import org.koin.dsl.module
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case.PracticeCreateDeletePracticeUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case.PracticeCreateLoadDataUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case.PracticeCreateSavePracticeUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.use_case.PracticeCreateValidateCharactersUseCase

val practiceCreateScreenModule = module {

    factory<PracticeCreateScreenContract.LoadDataUseCase> {
        PracticeCreateLoadDataUseCase(
            kanjiDataRepository = get(),
            practiceRepository = get()
        )
    }

    factory<PracticeCreateScreenContract.ValidateCharactersUseCase> {
        PracticeCreateValidateCharactersUseCase(
            kanjiDataRepository = get()
        )
    }

    factory<PracticeCreateScreenContract.SavePracticeUseCase> {
        PracticeCreateSavePracticeUseCase(
            practiceRepository = get()
        )
    }

    factory<PracticeCreateScreenContract.DeletePracticeUseCase> {
        PracticeCreateDeletePracticeUseCase(
            practiceRepository = get()
        )
    }

    factory<PracticeCreateScreenContract.ViewModel> {
        PracticeCreateViewModel(
            viewModelScope = it.component1(),
            loadDataUseCase = get(),
            validateCharactersUseCase = get(),
            savePracticeUseCase = get(),
            deletePracticeUseCase = get(),
            analyticsManager = get()
        )
    }

}