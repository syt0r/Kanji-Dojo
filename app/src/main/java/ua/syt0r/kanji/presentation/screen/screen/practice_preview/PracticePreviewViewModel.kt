package ua.syt0r.kanji.presentation.screen.screen.practice_preview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.presentation.screen.screen.practice_preview.PracticePreviewScreenContract.State
import javax.inject.Inject

@HiltViewModel
class PracticePreviewViewModel @Inject constructor(
    private val usedDataRepository: UserDataContract.PracticeRepository
) : ViewModel(), PracticePreviewScreenContract.ViewModel {

    override val state = MutableLiveData<State>(State.Init)

    override fun loadPracticeInfo(practiceId: Long) {

        fetchData(practiceId)
            .flowOn(Dispatchers.IO)
            .onStart {
                state.value = State.Loading
            }
            .take(1)
            .onEach {
                state.value = it
            }
            .launchIn(viewModelScope)

    }

    private fun fetchData(practiceId: Long): Flow<State.Loaded> = flow {
        emit(
            State.Loaded(
                practiceId = practiceId,
                kanjiList = usedDataRepository.getKanjiForPracticeSet(practiceId)
            )
        )
    }

}