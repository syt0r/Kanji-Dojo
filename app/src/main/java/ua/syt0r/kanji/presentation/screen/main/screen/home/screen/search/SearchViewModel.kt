package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreenContract.ScreenState
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val processInputUseCase: SearchScreenContract.ProcessInputUseCase
) : ViewModel(), SearchScreenContract.ViewModel {

    private val searchQueriesChannel = Channel<String>(Channel.BUFFERED)

    override val state = mutableStateOf(ScreenState(isLoading = false))

    init {
        searchQueriesChannel.consumeAsFlow()
            .onEach {
                state.value = state.value.copy(isLoading = true)
            }
            .debounce(300)
            .onEach { input ->
                state.value = processInputUseCase.process(input)
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    override fun search(input: String) {
        searchQueriesChannel.trySend(input)
    }

}