package ua.syt0r.kanji.core.user_data

import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.readUserVersion
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import ua.syt0r.kanji.core.userdata.db.PracticeQueries
import java.io.File
import java.io.InputStream

interface UserDataDatabaseManager {

    val dataChangedFlow: SharedFlow<Unit>

    suspend fun <T> runTransaction(
        notifyDataChange: Boolean = false,
        block: PracticeQueries.() -> T
    ): T

    suspend fun doWithSuspendedConnection(
        scope: suspend (info: UserDatabaseInfo) -> Unit
    )

    suspend fun replaceDatabase(inputStream: InputStream)

}

class UserDatabaseInfo(
    val version: Long,
    val file: File
)

abstract class BaseUserDataDatabaseManager(
    coroutineScope: CoroutineScope
) : UserDataDatabaseManager {

    protected data class DatabaseConnection(
        val sqlDriver: SqlDriver,
        val database: UserDataDatabase
    )

    private val activeDatabaseConnection = MutableStateFlow<DatabaseConnection?>(null)
    private val onDataUpdatedFlow = MutableSharedFlow<Unit>()

    override val dataChangedFlow: SharedFlow<Unit> = onDataUpdatedFlow

    init {
        coroutineScope.launch(Dispatchers.IO) {
            activeDatabaseConnection.value = createDatabaseConnection()
        }
    }

    protected fun getMigrationCallbacks(): Array<AfterVersion> = arrayOf(
        AfterVersion(3) { UserDataDatabaseMigrationAfter3.handleMigrations(it) },
        AfterVersion(4) { UserDataDatabaseMigrationAfter4.handleMigrations(it) }
    )

    protected abstract suspend fun createDatabaseConnection(): DatabaseConnection

    protected abstract fun getDatabaseFile(): File

    override suspend fun <T> runTransaction(
        notifyDataChange: Boolean,
        block: PracticeQueries.() -> T
    ): T {
        val result = withContext(Dispatchers.IO) {
            val queries = activeDatabaseConnection.filterNotNull()
                .first()
                .database
                .practiceQueries
            queries.transactionWithResult { queries.block() }
        }

        if (notifyDataChange) onDataUpdatedFlow.emit(Unit)

        return result
    }

    override suspend fun doWithSuspendedConnection(
        scope: suspend (info: UserDatabaseInfo) -> Unit
    ) {
        val info = getActiveDatabaseInfo()
        closeCurrentConnection()
        scope(info)
        activeDatabaseConnection.value = createDatabaseConnection()
    }

    override suspend fun replaceDatabase(inputStream: InputStream) {
        doWithSuspendedConnection {
            val databaseFile = getDatabaseFile()
            databaseFile.delete()
            inputStream.use { it.transferTo(databaseFile.outputStream()) }
        }
        onDataUpdatedFlow.emit(Unit)
    }

    private fun closeCurrentConnection() {
        val currentData = activeDatabaseConnection.value ?: return
        currentData.sqlDriver.close()
    }

    private suspend fun getActiveDatabaseInfo(): UserDatabaseInfo {
        return UserDatabaseInfo(
            version = activeDatabaseConnection.filterNotNull().first().sqlDriver.readUserVersion(),
            file = getDatabaseFile()
        )
    }

}
