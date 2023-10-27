package ua.syt0r.kanji.core.user_data

import android.app.Application
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase

class UserDataDatabaseProviderAndroid(
    private val app: Application
) : UserDataDatabaseProvider {

    private val context = CoroutineScope(context = Dispatchers.IO)

    override fun provideAsync(): Deferred<UserDataDatabase> {
        return context.async {
            val driver = AndroidSqliteDriver(
                schema = UserDataDatabase.Schema.synchronous(),
                context = app,
                name = "user_data",
                callback = AndroidSqliteDriver.Callback(
                    UserDataDatabase.Schema.synchronous(),
                    AfterVersion(3) {
                        runBlocking { UserDataDatabaseMigrationAfter3.handleMigrations(it) }
                    }
                )
            )
            UserDataDatabase(driver)
        }
    }

}