package ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.logger.Logger
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
        Logger.d("sending input $input")
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

    private fun handleSearchQueries() = viewModelScope.launch {
        searchQueriesChannel.consumeAsFlow()
            .distinctUntilChanged()
            .onEach { state.value = state.value.copy(isLoading = true) }
            .debounce(1000)
            .collectLatest { input ->
                kotlin.runCatching {
                    /***
                     * Doesn't interrupt, TODO fix + remove debounce
                     * More details: https://github.com/Kotlin/kotlinx.coroutines/issues/3109
                     */
                    Logger.d("start searching for $input")
                    val updatedState = runInterruptible(coroutineContext + Dispatchers.IO) {
                        Logger.d("processing input $input in background")
                        processInputUseCase.process(input)
                    }
                    Logger.d("finished searching for $input")
                    state.value = updatedState
                }.onFailure { Logger.d("search for $input was interrupted") }
            }
    }

    private fun handleRadicalsLoading(radicalsLoadedCompletable: CompletableDeferred<Unit>) {
        radicalsDataInitialLoadChannel.consumeAsFlow()
            .take(1)
            .onEach {
                val updatedState = withContext(Dispatchers.IO) {
                    RadicalSearchState(
                        radicalsListItems = loadRadicalsUseCase.load(),
                        characterListItems = emptyList(),
                        isLoading = false
                    )
                }
                radicalsState.value = updatedState
                radicalsLoadedCompletable.complete(Unit)
            }
            .launchIn(viewModelScope)
    }

    private fun handleRadicalSearchQueries(
        radicalsLoadedCompletable: CompletableDeferred<Unit>
    ) = viewModelScope.launch {
        radicalsSearchQueriesChannel.consumeAsFlow()
            .distinctUntilChanged()
            .onStart { radicalsLoadedCompletable.await() }
            .onEach { radicalsState.value = radicalsState.value.copy(isLoading = true) }
            .debounce(500)
            .collectLatest { radicals ->
                val currentState = radicalsState.value

                val updatedState = withContext(Dispatchers.IO) {
                    val searchResult = searchByRadicalsUseCase.search(radicals)
                    val updatedRadicals = updateEnabledRadicalsUseCase.update(
                        currentState.radicalsListItems,
                        searchResult
                    )
                    RadicalSearchState(
                        radicalsListItems = updatedRadicals,
                        characterListItems = searchResult.listData,
                        isLoading = false
                    )
                }

                radicalsState.value = updatedState
            }
    }

}