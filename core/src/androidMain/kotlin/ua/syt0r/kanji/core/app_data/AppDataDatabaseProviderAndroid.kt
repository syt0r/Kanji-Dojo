package ua.syt0r.kanji.core.app_data

import android.app.Application
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.app_data.db.AppDataDatabase
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.readUserVersion
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

private const val AppDataDatabaseName = "kanji_data"

class AppDataDatabaseProviderAndroid(
    private val app: Application
) : AppDataDatabaseProvider {

    private val context = CoroutineScope(context = Dispatchers.IO)

    override fun provideAsync(): Deferred<AppDataDatabase> = context.async {
        Logger.d(">>")
        val dbFile = app.getDatabasePath(AppDataDatabaseName)

        if (!dbFile.exists()) {
            recreateDatabase(dbFile)
        }

        val driver = AndroidSqliteDriver(
            schema = AppDataDatabase.Schema,
            context = app,
            name = dbFile.name,
            callback = object : AndroidSqliteDriver.Callback(AppDataDatabase.Schema) {
                override fun onDowngrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int
                ) {
                    /***
                     * Ignoring downgrade since sqldelight schema thinks db version is 1 because
                     * there are no migrations
                     */
                }
            }
        )

        val dbVersion = driver.readUserVersion()
        Logger.d("dbVersion[$dbVersion]")

        if (dbVersion != AppDataDatabaseVersion) {
            Logger.d("Database with version[$dbVersion] is outdated, copying DB from assets")
            recreateDatabase(dbFile)
        }

        AppDataDatabase(driver).also { Logger.d("<<") }
    }

    private suspend fun recreateDatabase(dbFile: File) {
        app.deleteDatabase(AppDataDatabaseName)
        val input = app.assets.open(AppDataDatabaseResourceName)
        val path = dbFile.toPath()
        withContext(Dispatchers.IO) {
            Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING)
        }
    }

}