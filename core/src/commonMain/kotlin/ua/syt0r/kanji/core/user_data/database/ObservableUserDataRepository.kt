package ua.syt0r.kanji.core.user_data.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.mergeSharedFlows
import ua.syt0r.kanji.core.userdata.db.UserDataQueries
import kotlin.time.measureTime

open class ObservableUserDataRepository(
    private val databaseManager: UserDataDatabaseContract.Manager,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ObservableRepository, UserDataDatabaseContract.TransactionScope {

    private val _changesFlow = MutableSharedFlow<Unit>()
    override val changesFlow: SharedFlow<Unit> = mergeSharedFlows(
        coroutineScope,
        _changesFlow,
        databaseManager.databaseChangeEvents
    )

    override suspend fun <T> readTransaction(block: UserDataQueries.() -> T): T =
        databaseManager.readTransaction(block)

    override suspend fun <T> writeTransaction(block: UserDataQueries.() -> T): T =
        databaseManager.writeTransaction(block).also { _changesFlow.emit(Unit) }

}

class CachedUserDataState<T>(
    resetFlow: SharedFlow<Unit>,
    private val provider: suspend () -> T,
    private val debugTitle: String,
    private val isLazy: Boolean = true,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val _data = MutableStateFlow(coroutineScope.createDeferred())
    val data: StateFlow<Deferred<T>> = _data
    suspend fun await() = data.first().await()

    init {
        resetFlow
            .onEach {
                Logger.d("Resetting cache for $debugTitle")
                coroutineScope { _data.emit(createDeferred()) }
            }
            .launchIn(coroutineScope)
    }

    private fun CoroutineScope.createDeferred(): Deferred<T> {
        return async(
            start = if (isLazy) CoroutineStart.LAZY else CoroutineStart.DEFAULT
        ) {
            Logger.d("Updating cache for $debugTitle")
            val value: T
            val time = measureTime { value = provider() }
            Logger.d("Updated cache for $debugTitle, time[$time]")
            value
        }
    }

}