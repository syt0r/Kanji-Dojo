package ua.syt0r.kanji.core

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.app_data.AppDataDatabaseProvider
import ua.syt0r.kanji.core.app_data.AppDataDatabaseResourceName
import ua.syt0r.kanji.core.app_data.AppDataDatabaseVersion
import ua.syt0r.kanji.core.app_data.db.AppDataDatabase
import ua.syt0r.kanji.core.logger.Logger

class IosAppDataDatabaseProvider : AppDataDatabaseProvider {

    private val coroutineScope = CoroutineScope(context = Dispatchers.IO)

    private val databasePath = Path(getPrivateAppDataDirPath())
    private val databaseName = "app_data.sqlite"

    @OptIn(ExperimentalResourceApi::class)
    override fun provideAsync(): Deferred<AppDataDatabase> = coroutineScope.async {
        SystemFileSystem.createDirectories(databasePath)

        val databaseFile = Path(databasePath, databaseName)

        if (!SystemFileSystem.exists(databaseFile)) {
            copyDatabaseFromResources()
        }

        val sqlDriver = newDriverConnection()
        val currentAppDataDBVersion = sqlDriver.readUserVersion()
        Logger.d("currentAppDataDBVersion[$currentAppDataDBVersion]")

        if (currentAppDataDBVersion != AppDataDatabaseVersion) {
            sqlDriver.close()
            copyDatabaseFromResources()
            AppDataDatabase(newDriverConnection())
        } else {
            AppDataDatabase(sqlDriver)
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    private fun newDriverConnection(): SqlDriver {
        return NativeSqliteDriver(
            schema = AppDataDatabase.Schema,
            name = databaseName,
            onConfiguration = { config ->
                config.copy(
                    version = AppDataDatabaseVersion.toInt(),
                    extendedConfig = DatabaseConfiguration.Extended(
                        basePath = getPrivateAppDataDirPath()
                    )
                )
            }
        )
    }

    @OptIn(ExperimentalResourceApi::class)
    private fun copyDatabaseFromResources() {
        val prepackedPath = Path(
            Res.getUri("files/$AppDataDatabaseResourceName").formattedIosFilePath()
        )
        val destinationPath = Path(databasePath, databaseName)

        val source = SystemFileSystem.source(prepackedPath)
        val sink = SystemFileSystem.sink(destinationPath)

        sink.buffered().apply {
            transferFrom(source)
            flush()
        }
    }

}

