package ua.syt0r.kanji.presentation.screen.screen.practice_create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.practice_create.CreateWritingPracticeScreenContract.State
import ua.syt0r.kanji.presentation.screen.screen.practice_create.CreateWritingPracticeScreenContract.StateType
import ua.syt0r.kanji.presentation.screen.screen.practice_create.data.EnteredKanji
import ua.syt0r.kanji_db_model.isKanji
import javax.inject.Inject

@HiltViewModel
class CreateWritingPracticeViewModel @Inject constructor(
    private val kanjiDataRepository: KanjiDataContract.Repository,
    private val practiceRepository: UserDataContract.PracticeRepository
) : ViewModel(), CreateWritingPracticeScreenContract.ViewModel {

    override val state = MutableLiveData<State>()

    override fun initialize(initialKanjiList: List<String>) {
        if (state.value == null) {
            state.value = State(
                data = initialKanjiList.map { EnteredKanji(it, true) }.toSet(),
                stateType = StateType.Loaded
            )
        }
    }

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
            EnteredKanji(kanji = it, isKnown = strokes.isNotEmpty())
        }.toSet()
        emit(currentData + newData)
    }

    private fun saveUseInput(title: String) = flow<Unit> {
        val kanji = state.value!!.data
            .filter { it.isKnown }
            .map { it.kanji }

        practiceRepository.createPracticeSet(
            kanjiList = kanji,
            setName = title
        )
    }

}