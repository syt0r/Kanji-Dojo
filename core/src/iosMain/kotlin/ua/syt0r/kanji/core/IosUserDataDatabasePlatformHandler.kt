package ua.syt0r.kanji.core

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.asSource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem
import ua.syt0r.kanji.core.file.PlatformFile
import ua.syt0r.kanji.core.user_data.database.DatabaseConnection
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase

class IosUserDataDatabasePlatformHandler(
    private val migrationProvider: UserDataDatabaseContract.MigrationProvider
) : UserDataDatabaseContract.PlatformHandler {

    companion object {
        private const val DB_NAME = "user_data.sqlite"
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun newConnection(): DatabaseConnection {
        val sqlDriver = NativeSqliteDriver(
            schema = UserDataDatabase.Schema,
            name = DB_NAME,
            onConfiguration = {
                it.copy(
                    extendedConfig = DatabaseConfiguration.Extended(
                        basePath = getPrivateAppDataDirPath()
                    )
                )
            },
            callbacks = migrationProvider()
        )

        return DatabaseConnection(
            sqlDriver = sqlDriver,
            UserDataDatabase(driver = sqlDriver)
        )
    }

    override fun getDatabaseAsFile(): PlatformFile {
        return PlatformFile(getPrivateAppDataDirPath() + "/" + DB_NAME)
    }

    override suspend fun replaceDatabaseFile(content: ByteReadChannel) {
        SystemFileSystem.sink(getDatabaseAsFile().path).buffered().apply {
            transferFrom(content.asSource())
            flush()
            close()
        }
    }

}
