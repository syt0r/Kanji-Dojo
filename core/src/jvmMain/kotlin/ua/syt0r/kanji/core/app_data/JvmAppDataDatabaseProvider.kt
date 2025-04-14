package ua.syt0r.kanji.core.app_data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktor.http.decodeURLPart
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ua.syt0r.kanji.Res
import ua.syt0r.kanji.core.app_data.db.AppDataDatabase
import ua.syt0r.kanji.core.getUserDataDirectory
import ua.syt0r.kanji.core.logger.Logger
import ua.syt0r.kanji.core.readUserVersion
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class JvmAppDataDatabaseProvider(
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppDataDatabaseProvider {

    private val context = CoroutineScope(dispatcher)

    @OptIn(ExperimentalResourceApi::class)
    override fun provideAsync(): Deferred<AppDataDatabase> = context.async {
        val databaseDirectory = getUserDataDirectory()
        databaseDirectory.mkdirs()
        val databaseFile = File(databaseDirectory, "kanji_data.sqlite")

        if (!databaseFile.exists()) {
            copyDatabaseFromResources(databaseFile)
        }

        val sqlDriver = newDriverConnection(databaseFile)
        val currentAppDataDBVersion = sqlDriver.readUserVersion()
        Logger.d("currentAppDataDBVersion[$currentAppDataDBVersion]")

        if (currentAppDataDBVersion != AppDataDatabaseVersion) {
            sqlDriver.close()
            copyDatabaseFromResources(databaseFile)
            AppDataDatabase(newDriverConnection(databaseFile))
        } else {
            AppDataDatabase(sqlDriver)
        }
    }

    private fun newDriverConnection(databaseFile: File): SqlDriver {
        return JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
    }

    @OptIn(ExperimentalResourceApi::class)
    private fun copyDatabaseFromResources(destination: File) {
        val resourceDatabaseInputStream = Res.getUri("files/$AppDataDatabaseResourceName")
            .decodeURLPart()
            .let { uri ->
                when {
                    uri.startsWith("jar") -> {
                        val pathInsideJar = uri.substring(uri.lastIndexOf("composeResources"))
                        ClassLoader.getSystemResourceAsStream(pathInsideJar)!!
                    }

                    else -> {
                        File(URI(uri)).inputStream()
                    }
                }
            }

        resourceDatabaseInputStream.use {
            Files.copy(it, destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

}