package ua.syt0r.kanji.core.user_data.database

import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.readUserVersion
import ua.syt0r.kanji.core.user_data.database.use_case.UpdateLocalDataTimestampUseCase
import ua.syt0r.kanji.core.userdata.db.UserDataQueries


class DefaultUserDataDatabaseManager(
    private val databasePlatformHandler: UserDataDatabaseContract.PlatformHandler,
    private val updateLocalDataTimestampUseCase: UpdateLocalDataTimestampUseCase,
    private val queryDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserDataDatabaseContract.Manager {

    private val _databaseChangeEvents = MutableSharedFlow<Unit>()
    override val databaseChangeEvents: SharedFlow<Unit> = _databaseChangeEvents

    private var connection: DatabaseConnection? = null
    private val connectionMutex = Mutex()

    private suspend inline fun withLockedDatabaseConnection(block: DatabaseConnection.() -> Unit) {
        connectionMutex.lock()
        val connection = this.connection
            ?: databasePlatformHandler.newConnection().also { connection = it }
        block(connection)
        connectionMutex.unlock()
    }

    override suspend fun <T> readTransaction(block: UserDataQueries.() -> T): T =
        runTransaction(false, block)

    override suspend fun <T> writeTransaction(block: UserDataQueries.() -> T): T =
        runTransaction(true, block)

    override suspend fun doWithSuspendedConnection(
        scope: suspend (info: UserDatabaseInfo) -> Unit
    ) {
        var result: Result<*>? = null
        withLockedDatabaseConnection {
            val info = getActiveDatabaseInfo()
            closeConnection()
            connection = null
            result = runCatching { scope(info) }
        }
        result!!.exceptionOrNull()?.let { throw it }
    }

    override suspend fun replaceDatabase(byteReadChannel: ByteReadChannel) {
        doWithSuspendedConnection { databasePlatformHandler.replaceDatabaseFile(byteReadChannel) }
        _databaseChangeEvents.emit(Unit)
    }

    private suspend fun <T> runTransaction(
        isWritingChanges: Boolean,
        block: UserDataQueries.() -> T
    ): T {
        var result: T? = null
        withLockedDatabaseConnection {
            result = withContext(queryDispatcher) {
                val queries = database.userDataQueries
                queries.transactionWithResult { queries.block() }
            }
        }
        if (isWritingChanges) updateLocalDataTimestampUseCase()
        return result!!
    }

    private suspend fun DatabaseConnection.getActiveDatabaseInfo(): UserDatabaseInfo {
        return UserDatabaseInfo(
            version = sqlDriver.readUserVersion(),
            file = databasePlatformHandler.getDatabaseAsFile()
        )
    }

}
