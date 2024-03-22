package ua.syt0r.kanji.presentation.screen.main.screen.feedback

import org.koin.dsl.module

val feedbackScreenModule = module {

    factory<FeedbackScreenContract.ViewModel> {
        FeedbackViewModel(
            viewModelScope = it.component1(),
            feedbackManager = get()
        )
    }

}