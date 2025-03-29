package ua.syt0r.kanji.core

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import io.ktor.http.decodeURLPart
import io.ktor.utils.io.ByteReadChannel
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import ua.syt0r.kanji.core.file.PlatformFile
import ua.syt0r.kanji.core.logger.Logger
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
        val bundleId = NSBundle.mainBundle.bundleIdentifier
        val databaseUrl = NSFileManager.defaultManager
            .URLsForDirectory(NSApplicationSupportDirectory, NSUserDomainMask)
            .first()
            .toString()
            .plus(bundleId)
        Logger.d("databasePath[$databaseUrl]")

        NSFileManager.defaultManager.createDirectoryAtURL(
            url = NSURL.URLWithString(databaseUrl)!!,
            withIntermediateDirectories = true,
            attributes = null,
            error = null
        )

        val sqlDriver = NativeSqliteDriver(
            schema = UserDataDatabase.Schema,
            name = DB_NAME,
            onConfiguration = {
                it.copy(
                    extendedConfig = DatabaseConfiguration.Extended(
                        basePath = databaseUrl.decodeURLPart()
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
        TODO("Not yet implemented")
    }

    override suspend fun replaceDatabaseFile(content: ByteReadChannel) {
        TODO("Not yet implemented")
    }

}
