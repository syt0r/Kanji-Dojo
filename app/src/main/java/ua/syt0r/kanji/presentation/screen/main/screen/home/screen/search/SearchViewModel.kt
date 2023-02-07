package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data.RadicalSearchState
import java.util.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val processInputUseCase: SearchScreenContract.ProcessInputUseCase,
    private val loadRadicalsUseCase: SearchScreenContract.LoadRadicalsUseCase,
    private val searchByRadicalsUseCase: SearchScreenContract.SearchByRadicalsUseCase,
    private val updateEnabledRadicalsUseCase: SearchScreenContract.UpdateEnabledRadicalsUseCase,
    private val analyticsManager: AnalyticsManager
) : ViewModel(), SearchScreenContract.ViewModel {

    private val searchQueriesChannel = Channel<String>(Channel.BUFFERED)

    private val radicalsDataInitialLoadChannel = Channel<Unit>(Channel.BUFFERED)
    private val radicalsLoadedCompletable = CompletableDeferred<Unit>()
    private val radicalsSearchQueriesChannel = Channel<Set<String>>(Channel.BUFFERED)

    override val state = mutableStateOf(ScreenState(isLoading = false))
    override val radicalsState = mutableStateOf(
        RadicalSearchState(
            radicalsListItems = emptyList(),
            characterListItems = emptyList(),
            isLoading = true
        )
    )


    init {
        handleSearchQueries()
        handleRadicalsLoading(radicalsLoadedCompletable)
        handleRadicalSearchQueries(radicalsLoadedCompletable)
    }

    override fun search(input: String) {
        searchQueriesChannel.trySend(input)
    }

    override fun loadRadicalsData() {
        radicalsDataInitialLoadChannel.trySend(Unit)
    }

    override fun radicalsSearch(radicals: Set<String>) {
        radicalsSearchQueriesChannel.trySend(radicals)
    }

    override fun reportScreenShown() {
        analyticsManager.setScreen("search")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleSearchQueries() {
        searchQueriesChannel.consumeAsFlow()
            .distinctUntilChanged()
            .onEach { state.value = state.value.copy(isLoading = true) }
            .flowOn(Dispatchers.Main)
            .debounce(300)
            .mapLatest { input -> processInputUseCase.process(input) }
            .flowOn(Dispatchers.IO)
            .onEach { state.value = it }
            .flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }

    private fun handleRadicalsLoading(radicalsLoadedCompletable: CompletableDeferred<Unit>) {
        radicalsDataInitialLoadChannel.consumeAsFlow()
            .take(1)
            .map {
                val radicalsDataList = withContext(Dispatchers.IO) { loadRadicalsUseCase.load() }
                RadicalSearchState(
                    radicalsListItems = radicalsDataList,
                    characterListItems = emptyList(),
                    isLoading = false
                )
            }
            .flowOn(Dispatchers.IO)
            .onEach {
                radicalsState.value = it
                radicalsLoadedCompletable.complete(Unit)
            }
            .flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleRadicalSearchQueries(radicalsLoadedCompletable: CompletableDeferred<Unit>) {
        radicalsSearchQueriesChannel.consumeAsFlow()
            .distinctUntilChanged()
            .onEach { radicalsState.value = radicalsState.value.copy(isLoading = true) }
            .onStart { radicalsLoadedCompletable.await() }
            .flowOn(Dispatchers.Main)
            .debounce(300)
            .mapLatest { radicals ->
                val currentState = radicalsState.value

                val searchResult = searchByRadicalsUseCase.search(radicals)
                val updatedRadicals = updateEnabledRadicalsUseCase.update(
                    currentState.radicalsListItems,
                    searchResult
                )

                searchResult to updatedRadicals
            }
            .flowOn(Dispatchers.IO)
            .onEach { (searchResult, updatedRadicals) ->
                radicalsState.value = RadicalSearchState(
                    radicalsListItems = updatedRadicals,
                    characterListItems = searchResult.listData,
                    isLoading = false
                )
            }
            .flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)
    }

}