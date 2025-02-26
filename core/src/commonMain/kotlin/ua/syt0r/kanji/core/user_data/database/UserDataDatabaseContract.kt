package ua.syt0r.kanji.core.user_data.database

import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ua.syt0r.kanji.core.userdata.db.UserDataQueries
import java.io.File
import java.io.InputStream
import kotlin.coroutines.CoroutineContext

interface UserDataDatabaseContract {

    interface TransactionScope {
        suspend fun <T> readTransaction(block: UserDataQueries.() -> T): T
        suspend fun <T> writeTransaction(block: UserDataQueries.() -> T): T
    }

    interface Manager : TransactionScope {
        val databaseChangeEvents: SharedFlow<Unit>
        suspend fun doWithSuspendedConnection(scope: suspend (info: UserDatabaseInfo) -> Unit)
        suspend fun replaceDatabase(inputStream: InputStream)
    }

    interface PlatformHandler {
        val connectionContext: CoroutineContext
        val queryContext: CoroutineContext
        suspend fun newConnection(): DatabaseConnection
        fun getDatabaseFile(): File
    }

    interface Migration {
        val version: Long
        suspend fun execute(driver: SqlDriver)
    }

    interface MigrationProvider {
        operator fun invoke(): Array<AfterVersion>
    }

    interface MigrationObservable {
        val state: StateFlow<DatabaseMigrationState>
        fun updateState(updatedState: DatabaseMigrationState)
    }

}