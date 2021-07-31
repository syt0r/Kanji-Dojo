package ua.syt0r.kanji.presentation.screen.screen.writing_practice_create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.writing_practice_create.CreateWritingPracticeScreenContract.*
import ua.syt0r.kanji_db_model.isKanji
import javax.inject.Inject

@HiltViewModel
class CreateWritingPracticeViewModel @Inject constructor(
    private val kanjiDataRepository: KanjiDataContract.Repository,
    private val writingRepository: UserDataContract.WritingRepository
) : ViewModel(), CreateWritingPracticeScreenContract.ViewModel {

    override val state = MutableLiveData<State>(
        State(
            data = emptySet(),
            stateType = StateType.Loaded
        )
    )

    override fun submitUserInput(input: String) {
        parseKanji(input)
            .flatMapLatest { verifyKanjiInDatabase(it) }
            .flowOn(Dispatchers.IO)
            .onStart {
                state.value = state.value!!.copy(
                    stateType = StateType.Loading
                )
            }
            .onEach {
                state.value = State(
                    data = it,
                    stateType = StateType.Loaded
                )
            }
            .launchIn(viewModelScope)
    }

    override fun createSet(title: String) {
        saveUseInput(title).flowOn(Dispatchers.IO)
            .onStart {
                state.value = state.value!!.copy(
                    stateType = StateType.Saving
                )
            }
            .onCompletion {
                state.value = state.value!!.copy(
                    stateType = StateType.Done
                )
            }
            .launchIn(viewModelScope)
    }

    private fun parseKanji(input: String): Flow<List<String>> = flow {
        val kanjiList = input.toCharArray()
            .filter { it.isKanji() }
            .map { it.toString() }
        emit(kanjiList)
    }

    private fun verifyKanjiInDatabase(kanjiList: List<String>): Flow<Set<EnteredKanji>> = flow {
        val currentData = state.value!!.data
        val newData = kanjiList.map {
            val strokes = kanjiDataRepository.getStrokes(it)
            when (strokes.isEmpty()) {
                true -> EnteredKanji.Unknown(it)
                false -> EnteredKanji.Known(it)
            }
        }.toSet()
        emit(currentData + newData)
    }

    private fun saveUseInput(title: String) = flow<Unit> {
        val kanji = state.value!!.data
            .filterIsInstance(EnteredKanji.Known::class.java)
            .map { it.kanji }

        writingRepository.createPracticeSet(
            kanjiList = kanji,
            setName = title
        )
    }

}