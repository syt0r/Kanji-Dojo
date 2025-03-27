package ua.syt0r.kanji.core.user_data

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.ktor.util.cio.readChannel
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.user_data.database.DatabaseConnection
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase

class AndroidUserDataDatabasePlatformHandler(
    private val context: Context,
    private val migrationProvider: UserDataDatabaseContract.MigrationProvider
) : UserDataDatabaseContract.PlatformHandler {

    companion object {
        private const val DEFAULT_DB_NAME = "user_data"
    }

    override suspend fun newConnection(): DatabaseConnection = withContext(Dispatchers.Main) {
        val driver = AndroidSqliteDriver(
            schema = UserDataDatabase.Schema,
            context = context,
            name = DEFAULT_DB_NAME,
            callback = AndroidSqliteDriver.Callback(
                UserDataDatabase.Schema,
                *migrationProvider()
            )
        )
        DatabaseConnection(
            sqlDriver = driver,
            database = UserDataDatabase(driver)
        )
    }

    override fun readDatabaseFile(): ByteReadChannel {
        return context.getDatabasePath(DEFAULT_DB_NAME)!!.readChannel()
    }

}