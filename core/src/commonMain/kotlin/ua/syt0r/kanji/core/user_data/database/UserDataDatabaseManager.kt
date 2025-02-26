package ua.syt0r.kanji.core.user_data.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.readUserVersion
import ua.syt0r.kanji.core.transferToCompat
import ua.syt0r.kanji.core.user_data.database.use_case.UpdateLocalDataTimestampUseCase
import ua.syt0r.kanji.core.userdata.db.UserDataQueries
import java.io.InputStream


class DefaultUserDataDatabaseManager(
    private val databasePlatformHandler: UserDataDatabaseContract.PlatformHandler,
    private val updateLocalDataTimestampUseCase: UpdateLocalDataTimestampUseCase
) : UserDataDatabaseContract.Manager {

    private data class ManagerState(
        val connection: Deferred<DatabaseConnection>
    )

    private val _databaseChangeEvents = MutableSharedFlow<Unit>()
    override val databaseChangeEvents: SharedFlow<Unit> = _databaseChangeEvents

    private val connectionScope = CoroutineScope(databasePlatformHandler.connectionContext)
    private val _state = MutableStateFlow<ManagerState?>(value = createState())

    override suspend fun <T> readTransaction(block: UserDataQueries.() -> T): T =
        runTransaction(false, block)

    override suspend fun <T> writeTransaction(block: UserDataQueries.() -> T): T =
        runTransaction(true, block)

    override suspend fun doWithSuspendedConnection(
        scope: suspend (info: UserDatabaseInfo) -> Unit
    ) {
        val info = getActiveDatabaseInfo()
        closeCurrentConnection()
        val result = runCatching { scope(info) }
        _state.value = createState()
        result.exceptionOrNull()?.let { throw it }
    }

    override suspend fun replaceDatabase(inputStream: InputStream) {
        doWithSuspendedConnection {
            val databaseFile = databasePlatformHandler.getDatabaseFile()
            databaseFile.delete()
            inputStream.use { it.transferToCompat(databaseFile.outputStream()) }
        }
        _databaseChangeEvents.emit(Unit)
    }

    private suspend fun <T> runTransaction(
        isWritingChanges: Boolean,
        block: UserDataQueries.() -> T
    ): T {
        return withContext(databasePlatformHandler.queryContext) {
            val queries = waitDatabaseConnection().database.userDataQueries
            val result = queries.transactionWithResult { queries.block() }
            if (isWritingChanges) updateLocalDataTimestampUseCase()
            result
        }
    }

    private suspend fun closeCurrentConnection() {
        _state.value?.connection?.await()?.close()
        _state.value = null
    }

    private suspend fun getActiveDatabaseInfo(): UserDatabaseInfo {
        return UserDatabaseInfo(
            version = waitDatabaseConnection().sqlDriver.readUserVersion(),
            file = databasePlatformHandler.getDatabaseFile()
        )
    }

    private fun createState(): ManagerState {
        return ManagerState(
            connection = connectionScope.async { databasePlatformHandler.newConnection() }
        )
    }

    private suspend fun waitDatabaseConnection(): DatabaseConnection {
        return _state.filterNotNull().first().connection.await()
    }

}
