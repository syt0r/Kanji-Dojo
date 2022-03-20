package ua.syt0r.kanji.presentation.screen.screen.kanji_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.presentation.common.ui.kanji.parseKanjiStrokes
import ua.syt0r.kanji.presentation.screen.screen.kanji_info.KanjiInfoScreenContract.State
import ua.syt0r.kanji_db_model.db.KanjiReadingTable
import ua.syt0r.kanji_db_model.model.KanjiClassifications
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
        val readings = kanjiDataRepository.getReadings(kanji)
        emit(
            State.Loaded(
                kanji = kanji,
                strokes = parseKanjiStrokes(kanjiDataRepository.getStrokes(kanji)),
                meanings = kanjiDataRepository.getMeanings(kanji),
                on = readings.filter { it.value == KanjiReadingTable.ReadingType.ON }
                    .map { it.key },
                kun = readings.filter { it.value == KanjiReadingTable.ReadingType.KUN }
                    .map { it.key },
                jlptLevel = KanjiClassifications.JLPT.N5
            )
        )
    }

}