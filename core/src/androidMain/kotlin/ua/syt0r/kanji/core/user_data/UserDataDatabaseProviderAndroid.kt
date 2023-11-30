package ua.syt0r.kanji.core.user_data

import android.app.Application
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase

class UserDataDatabaseProviderAndroid(
    private val app: Application
) : UserDataDatabaseProvider {

    private val context = CoroutineScope(context = Dispatchers.IO)

    override fun provideAsync(): Deferred<UserDataDatabase> {
        return context.async {
            val driver = AndroidSqliteDriver(
                schema = UserDataDatabase.Schema,
                context = app,
                name = "user_data",
                callback = AndroidSqliteDriver.Callback(
                    UserDataDatabase.Schema,
                    *userDataDatabaseMigrationCallbacks
                )
            )
            UserDataDatabase(driver)
        }
    }

}