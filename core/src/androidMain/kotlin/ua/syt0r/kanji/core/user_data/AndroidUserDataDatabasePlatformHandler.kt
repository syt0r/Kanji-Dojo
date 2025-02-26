package ua.syt0r.kanji.core.user_data

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.Dispatchers
import ua.syt0r.kanji.core.user_data.database.DatabaseConnection
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import java.io.File
import kotlin.coroutines.CoroutineContext

class AndroidUserDataDatabasePlatformHandler(
    private val context: Context,
    private val migrationProvider: UserDataDatabaseContract.MigrationProvider
) : UserDataDatabaseContract.PlatformHandler {

    companion object {
        private const val DEFAULT_DB_NAME = "user_data"
    }

    override val connectionContext: CoroutineContext = Dispatchers.Main
    override val queryContext: CoroutineContext = Dispatchers.IO

    override suspend fun newConnection(): DatabaseConnection {
        val driver = AndroidSqliteDriver(
            schema = UserDataDatabase.Schema,
            context = context,
            name = DEFAULT_DB_NAME,
            callback = AndroidSqliteDriver.Callback(
                UserDataDatabase.Schema,
                *migrationProvider()
            )
        )
        return DatabaseConnection(
            sqlDriver = driver,
            database = UserDataDatabase(driver)
        )
    }

    override fun getDatabaseFile(): File {
        return context.getDatabasePath(DEFAULT_DB_NAME)!!
    }

}