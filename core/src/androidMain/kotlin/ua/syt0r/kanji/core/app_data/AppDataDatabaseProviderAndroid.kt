package ua.syt0r.kanji.core.app_data

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ua.syt0r.kanji.core.CustomVersionSqlSchema
import ua.syt0r.kanji.core.app_data.db.AppDataDatabase
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.readUserVersion
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

private const val AppDataDatabaseName = "kanji_data"

class AppDataDatabaseProviderAndroid(
    private val context: Context
) : AppDataDatabaseProvider {

    private val coroutineScope = CoroutineScope(context = Dispatchers.IO)

    override fun provideAsync(): Deferred<AppDataDatabase> = coroutineScope.async {
        getExistingDatabaseIfUpdated() ?: createNewDatabaseFromResources()
    }

    private suspend fun getExistingDatabaseIfUpdated(): AppDataDatabase? {
        val dbFile = context.getDatabasePath(AppDataDatabaseName)
        if (!dbFile.exists()) return null

        val result = createDriver(dbFile)

        if (result.wasUpgraded) {
            Logger.d("Database is outdated")
            result.driver.close()
            return null
        }

        return AppDataDatabase(result.driver)
    }

    private suspend fun createNewDatabaseFromResources(): AppDataDatabase {
        val isDeleted = context.deleteDatabase(AppDataDatabaseName)
        Logger.d("isDeleted[$isDeleted]")

        val input = context.assets.open(AppDataDatabaseResourceName)
        val dbFile = context.getDatabasePath(AppDataDatabaseName)
        val path = dbFile.toPath()
        withContext(Dispatchers.IO) { Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING) }

        val result = createDriver(dbFile)
        return AppDataDatabase(result.driver)
    }

    private data class DriverCreationResult(
        val driver: AndroidSqliteDriver,
        val wasUpgraded: Boolean
    )

    private suspend fun createDriver(dbFile: File): DriverCreationResult {
        val schema = CustomVersionSqlSchema(AppDataDatabaseVersion, AppDataDatabase.Schema)
        var wasUpgraded = false
        val onDatabaseOpen = CompletableDeferred<Unit>()
        val driver = AndroidSqliteDriver(
            schema = schema,
            context = context,
            name = dbFile.name,
            callback = object : AndroidSqliteDriver.Callback(schema) {
                override fun onUpgrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int
                ) {
                    super.onUpgrade(db, oldVersion, newVersion)
                    Logger.d("oldVersion[$oldVersion] newVersion[$newVersion]")
                    wasUpgraded = true
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    Logger.logMethod()
                    onDatabaseOpen.complete(Unit)
                }
            },
        )
        Logger.d("driver created")
        val version = driver.readUserVersion() // To trigger onOpen callback
        onDatabaseOpen.await()
        Logger.d("driver ready version[$version] wasUpgraded[$wasUpgraded]")
        return DriverCreationResult(driver, wasUpgraded)
    }
}