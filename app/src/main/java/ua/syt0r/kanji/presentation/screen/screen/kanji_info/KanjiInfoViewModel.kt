package ua.syt0r.kanji.presentation.screen.screen.kanji_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.KanjiInfoScreenContract.State
import javax.inject.Inject

@HiltViewModel
class KanjiInfoViewModel @Inject constructor(
    private val kanjiDataRepository: KanjiDataContract.Repository
) : ViewModel(), KanjiInfoScreenContract.ViewModel {

    override val state = MutableLiveData<State>(State.Init)

    override fun loadKanjiInfo(kanji: String) {
        if (state.value != State.Init) return

        fetchKanjiInfo(kanji)
            .flowOn(Dispatchers.IO)
            .onStart { state.value = State.Loading }
            .onEach { state.value = it }
            .launchIn(viewModelScope)
    }

    private fun fetchKanjiInfo(kanji: String): Flow<State.Loaded> = flow {
        emit(
            State.Loaded(
                kanji = kanji,
                strokes = kanjiDataRepository.getStrokes(kanji),
                meanings = kanjiDataRepository.getMeanings(kanji)
            )
        )
    }

}