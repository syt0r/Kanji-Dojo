package ua.syt0r.kanji.core.user_data.database

import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.readUserVersion
import ua.syt0r.kanji.core.user_data.database.use_case.UpdateLocalDataTimestampUseCase
import ua.syt0r.kanji.core.userdata.db.UserDataQueries


@OptIn(ExperimentalCoroutinesApi::class)
class DefaultUserDataDatabaseManager(
    private val databasePlatformHandler: UserDataDatabaseContract.PlatformHandler,
    private val updateLocalDataTimestampUseCase: UpdateLocalDataTimestampUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserDataDatabaseContract.Manager {

    private val _databaseChangeEvents = MutableSharedFlow<Unit>()
    override val databaseChangeEvents: SharedFlow<Unit> = _databaseChangeEvents

    private val coroutineScope = CoroutineScope(dispatcher)

    sealed interface DatabaseState {
        object Disconnected : DatabaseState
        object Connecting : DatabaseState
        data class Connected(
            val connection: DatabaseConnection
        ) : DatabaseState
    }

    private val state = MutableStateFlow<DatabaseState>(
        value = DatabaseState.Disconnected
    )

    init {
        coroutineScope.launch { connectToDatabase() }
    }

    private suspend fun <T> withConnectedDatabase(
        block: suspend DatabaseConnection.() -> T
    ): T {
        return state
            .filterIsInstance<DatabaseState.Connected>()
            .mapLatest { block.invoke(it.connection) }
            .flowOn(dispatcher)
            .first()
    }

    private suspend fun connectToDatabase() {
        state.value = DatabaseState.Connecting
        state.value = DatabaseState.Connected(
            connection = databasePlatformHandler.newConnection()
        )
    }

    override suspend fun <T> readTransaction(block: UserDataQueries.() -> T): T =
        runTransaction(false, block)

    override suspend fun <T> writeTransaction(block: UserDataQueries.() -> T): T =
        runTransaction(true, block)

    override suspend fun withDisconnectedDatabase(scope: suspend (info: UserDatabaseInfo) -> Unit) {
        withContext(dispatcher) {
            val databaseInfo = withConnectedDatabase {
                getActiveDatabaseInfo().also { closeConnection() }
            }

            state.value = DatabaseState.Disconnected
            val result = runCatching { scope(databaseInfo) }
            connectToDatabase()

            result.exceptionOrNull()?.let { throw it }
        }
    }

    override suspend fun replaceDatabase(byteReadChannel: ByteReadChannel) {
        withDisconnectedDatabase { databasePlatformHandler.replaceDatabaseFile(byteReadChannel) }
        Logger.d("notify database changed >>")
        _databaseChangeEvents.emit(Unit)
        Logger.d("notify database changed <<")
    }

    private suspend fun <T> runTransaction(
        isWritingChanges: Boolean,
        block: UserDataQueries.() -> T
    ): T {
        Logger.d(">> transaction isWritingChanges[$isWritingChanges]")
        var result = withConnectedDatabase {
            val queries = database.userDataQueries
            queries.transactionWithResult { block(queries) }
        }
        if (isWritingChanges) updateLocalDataTimestampUseCase()
        Logger.d("<< transaction isWritingChanges[$isWritingChanges]")
        return result
    }

    private suspend fun DatabaseConnection.getActiveDatabaseInfo(): UserDatabaseInfo {
        return UserDatabaseInfo(
            version = sqlDriver.readUserVersion(),
            file = databasePlatformHandler.getDatabaseAsFile()
        )
    }

}
