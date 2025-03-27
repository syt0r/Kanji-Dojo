package ua.syt0r.kanji.core.user_data.database

import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.SqlDriver
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ua.syt0r.kanji.core.userdata.db.UserDataQueries

interface UserDataDatabaseContract {

    interface TransactionScope {
        suspend fun <T> readTransaction(block: UserDataQueries.() -> T): T
        suspend fun <T> writeTransaction(block: UserDataQueries.() -> T): T
    }

    interface Manager : TransactionScope {
        val databaseChangeEvents: SharedFlow<Unit>
        suspend fun doWithSuspendedConnection(scope: suspend (info: UserDatabaseInfo) -> Unit)
        suspend fun replaceDatabase(byteReadChannel: ByteReadChannel)
    }

    interface PlatformHandler {
        suspend fun newConnection(): DatabaseConnection
        fun readDatabaseFile(): ByteReadChannel
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