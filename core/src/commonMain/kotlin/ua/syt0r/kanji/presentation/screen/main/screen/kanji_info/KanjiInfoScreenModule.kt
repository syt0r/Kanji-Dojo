package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info

import org.koin.dsl.module
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.use_case.KanjiInfoLoadDataUseCase

val kanjiInfoScreenModule = module {

    factory<KanjiInfoScreenContract.LoadDataUseCase> {
        KanjiInfoLoadDataUseCase(
            kanjiDataRepository = get(),
            analyticsManager = get()
        )
    }

    factory<KanjiInfoScreenContract.ViewModel> {
        KanjiInfoViewModel(
            viewModelScope = it.component1(),
            loadDataUseCase = get(),
            analyticsManager = get()
        )
    }

}