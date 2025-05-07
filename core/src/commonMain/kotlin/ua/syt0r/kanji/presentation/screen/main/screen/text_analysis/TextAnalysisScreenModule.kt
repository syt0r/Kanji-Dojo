package ua.syt0r.kanji.presentation.screen.main.screen.text_analysis

import org.koin.dsl.module
import ua.syt0r.kanji.presentation.multiplatformViewModel

val textAnalysisScreenModule = module {

    multiplatformViewModel {
        TextAnalysisViewModel(
            viewModelScope = it.component1(),
            accountManager = get(),
            repository = get(),
            networkApi = get()
        )
    }

}