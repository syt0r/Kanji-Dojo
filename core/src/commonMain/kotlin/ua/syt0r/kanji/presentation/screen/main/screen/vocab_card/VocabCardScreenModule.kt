package ua.syt0r.kanji.presentation.screen.main.screen.vocab_card

import org.koin.dsl.module
import ua.syt0r.kanji.presentation.multiplatformViewModel

val vocabCardScreenModule = module {

    multiplatformViewModel<VocabCardScreenContract.ViewModel> {
        VocabCardViewModel(
            viewModelScope = it.component1(),
            appDataRepository = get(),
            suggestedVocabCardData = it.component2()
        )
    }

}