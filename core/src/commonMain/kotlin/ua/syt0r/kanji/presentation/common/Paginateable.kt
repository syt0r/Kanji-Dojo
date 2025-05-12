package ua.syt0r.kanji.presentation.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.logger.Logger

interface Paginateable<T> {
    val total: Int
    val list: StateFlow<List<T>>
    val canLoadMore: StateFlow<Boolean>
    fun loadMore()
}

data class PaginateableState<T>(
    val paginateable: Paginateable<T>,
    val listState: State<List<T>>,
    private val canLoadMoreState: State<Boolean>
) {

    val total = paginateable.total
    val list: List<T> by listState
    val canLoadMore: Boolean by canLoadMoreState

    fun loadMore() = paginateable.loadMore()

}

@Composable
fun <T> Paginateable<T>.collectAsState(): PaginateableState<T> {
    val listState = list.collectAsState()
    val canLoadMoreState = canLoadMore.collectAsState()
    return remember(this) { PaginateableState(this, listState, canLoadMoreState) }
}

@Composable
fun PaginationLoadLaunchedEffect(
    listState: LazyListState,
    prefetchDistance: Int = 50,
    loadMore: () -> Unit
) {
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .map { it.isNearListEnd(prefetchDistance) }
            .filter { it }
            .collect { loadMore() }
    }
}

@Composable
fun PaginationLoadLaunchedEffect(
    listState: LazyListState,
    prefetchDistance: Int = 50,
    paginateableState: PaginateableState<out Any>
) {
    LaunchedEffect(paginateableState) {
        Logger.d("starting listening for load more for - ${paginateableState.list.firstOrNull()}")
        snapshotFlow { listState.layoutInfo }
            .map { it.isNearListEnd(prefetchDistance) }
            .filter { it }
            .collect { paginateableState.loadMore() }
    }
}

@Composable
fun PaginationLoadLaunchedEffect(
    listState: LazyListState,
    prefetchDistance: Int = 50,
    paginateableToExpandedStateList: List<Pair<Paginateable<*>, State<Boolean>>>
) {

    PaginationLoadLaunchedEffect(
        listState = listState,
        prefetchDistance = prefetchDistance,
        loadMore = {
            val loadMoreTargetData = paginateableToExpandedStateList
                .find { (paginateable, isExpandedState) ->
                    isExpandedState.value && paginateable.canLoadMore.value
                }
                ?.first

            loadMoreTargetData?.loadMore()
        }
    )

}

suspend fun <T> paginateable(
    coroutineScope: CoroutineScope,
    limit: Int,
    initial: List<T> = emptyList<T>(),
    loadMoreImmediately: Boolean = false,
    load: suspend (offset: Int) -> List<T>
): Paginateable<T> {

    var offset = 0
    val loadMoreRequestsChannel = Channel<Unit>(onBufferOverflow = BufferOverflow.DROP_LATEST)

    val list = MutableStateFlow<List<T>>(initial)

    val canLoadMoreState: StateFlow<Boolean> = list
        .map { it.size < limit }
        .stateIn(coroutineScope)

    val loadMoreAction = suspend {
        Logger.d("loading more data")
        withContext(Dispatchers.IO) {
            val extraData = load(offset)
            offset += extraData.size
            list.value = list.value.plus(extraData)
        }
        Logger.d("loading completed")
    }

    if (loadMoreImmediately) loadMoreAction()

    coroutineScope.launch {
        loadMoreRequestsChannel.consumeAsFlow().collect {
            canLoadMoreState.filter { it }.first()
            loadMoreAction()
        }
    }

    return object : Paginateable<T> {

        override val total: Int = limit
        override val list: StateFlow<List<T>> = list
        override val canLoadMore: StateFlow<Boolean> = canLoadMoreState

        override fun loadMore() {
            coroutineScope.launch { loadMoreRequestsChannel.send(Unit) }
        }

    }

}