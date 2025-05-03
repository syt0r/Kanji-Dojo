package ua.syt0r.kanji.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.presentation.LifecycleState
import kotlin.time.measureTime

sealed interface RefreshableData<T> {
    class Loading<T> : RefreshableData<T>
    data class Loaded<T>(val value: T) : RefreshableData<T>
}

inline fun <reified T> refreshableDataFlow(
    dataChangeFlow: Flow<Unit>,
    lifecycleState: StateFlow<LifecycleState>,
    noinline valueProvider: suspend CoroutineScope.() -> T
): Flow<RefreshableData<T>> = channelFlow {

    dataChangeFlow.onStart { emit(Unit) }
        .collectLatest {
            send(RefreshableData.Loading())
            waitForVisibility(lifecycleState)

            val value: T
            val loadingTime = measureTime { value = valueProvider.invoke(this) }
            Logger.d("Loaded ${T::class.qualifiedName} data, loadingTime[$loadingTime]")

            send(RefreshableData.Loaded(value))
        }

}.distinctLoading()


@OptIn(ExperimentalCoroutinesApi::class)
inline fun <reified T> refreshableDataProducerFlow(
    dataChangeFlow: Flow<Unit>,
    lifecycleState: StateFlow<LifecycleState>,
    noinline producer: suspend ProducerScope<T>.() -> Unit
): Flow<RefreshableData<T>> = dataChangeFlow
    .onStart { emit(Unit) }
    .flatMapLatest {
        channelFlow<T> {
            waitForVisibility(lifecycleState)
            producer(this)
        }
            .map<T, RefreshableData<T>> { RefreshableData.Loaded(it) }
            .onStart { emit(RefreshableData.Loading()) }
    }
    .distinctLoading()

suspend fun waitForVisibility(lifecycleState: StateFlow<LifecycleState>) {
    lifecycleState.filter { it == LifecycleState.Visible }.first()
}

fun <T> Flow<RefreshableData<T>>.distinctLoading(): Flow<RefreshableData<T>> {
    return distinctUntilChanged { old, new ->
        if (old::class == new::class && old::class == RefreshableData.Loading::class) true
        else old == new
    }
}
