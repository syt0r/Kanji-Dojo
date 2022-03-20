package ua.syt0r.kanji.presentation.screen.screen.writing_practice_import

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_import.ImportWritingPracticeScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_import.data.PracticeImportItem
import ua.syt0r.kanji_db_model.model.KanjiClassifications
import javax.inject.Inject

@HiltViewModel
class ImportWritingPracticeViewModel @Inject constructor(
    private val kanjiDataRepository: KanjiDataContract.Repository
) : ViewModel(), ImportWritingPracticeScreenContract.ViewModel {

    override val state = MutableLiveData<State>(State.Loading)

    init {
        flow { emit(kanjiDataRepository.getKanjiClassifications()) }
            .map { kanjiClassificationNames ->
                KanjiClassifications.JLPT.values()
                    .map { it.name }
                    .intersect(kanjiClassificationNames)
                    .map {
                        PracticeImportItem(
                            displayName = "JLPT ${it.uppercase()}",
                            value = it
                        )
                    }
            }
            .flowOn(Dispatchers.IO)
            .onEach { practiceItems ->
                state.value = State.Loaded(practiceItems)
            }
            .launchIn(viewModelScope)
    }

}