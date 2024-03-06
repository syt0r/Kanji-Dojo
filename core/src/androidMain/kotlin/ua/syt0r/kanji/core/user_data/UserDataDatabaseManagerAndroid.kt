package ua.syt0r.kanji.core.user_data

import android.app.Application
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase

class UserDataDatabaseManagerAndroid(
    private val app: Application,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : BaseUserDataDatabaseManager(coroutineScope) {

    companion object {
        private const val DEFAULT_DB_NAME = "user_data"
    }

    override suspend fun getDatabase(): DatabaseInstanceData {
        val driver = AndroidSqliteDriver(
            schema = UserDataDatabase.Schema,
            context = app,
            name = DEFAULT_DB_NAME,
            callback = AndroidSqliteDriver.Callback(
                UserDataDatabase.Schema,
                *getMigrationCallbacks()
            )
        )
        return DatabaseInstanceData(
            sqlDriver = driver,
            database = UserDataDatabase(driver)
        )
    }

}