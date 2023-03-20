package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info

import org.koin.dsl.module
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.use_case.KanjiInfoLoadCharacterWordsUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.use_case.KanjiInfoLoadDataUseCase

val kanjiInfoScreenModule = module {

    factory<KanjiInfoScreenContract.LoadDataUseCase> {
        KanjiInfoLoadDataUseCase(
            kanjiDataRepository = get(),
            analyticsManager = get()
        )
    }

    factory<KanjiInfoScreenContract.LoadCharacterWordsUseCase> {
        KanjiInfoLoadCharacterWordsUseCase(
            kanjiDataRepository = get()
        )
    }

    factory<KanjiInfoScreenContract.ViewModel> {
        KanjiInfoViewModel(
            viewModelScope = it.component1(),
            loadDataUseCase = get(),
            loadCharacterWordsUseCase = get(),
            analyticsManager = get()
        )
    }

}