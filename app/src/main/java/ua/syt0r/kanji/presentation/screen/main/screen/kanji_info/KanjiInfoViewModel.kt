package ua.syt0r.kanji.presentation.screen.main.screen.kanji_info

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.presentation.screen.main.screen.kanji_info.KanjiInfoScreenContract.ScreenState
import javax.inject.Inject

@HiltViewModel
class KanjiInfoViewModel @Inject constructor(
    private val loadDataUseCase: KanjiInfoScreenContract.LoadDataUseCase
) : ViewModel(), KanjiInfoScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun loadCharacterInfo(character: String) {
        val currentState = state.value
        if (currentState is ScreenState.Loaded) return

        viewModelScope.launch {
            state.value = withContext(Dispatchers.IO) {
                loadDataUseCase.load(character)
            }
        }
    }

}