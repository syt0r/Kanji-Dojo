package ua.syt0r.kanji.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import ua.syt0r.kanji.core.IosAppDataDatabaseProvider
import ua.syt0r.kanji.core.IosKanaTtsManager
import ua.syt0r.kanji.core.IosUserDataDatabasePlatformHandler
import ua.syt0r.kanji.core.app_data.AppDataDatabaseProvider
import ua.syt0r.kanji.core.backup.BackupArchiveHandler
import ua.syt0r.kanji.core.backup.IosBackupArchiveHandler
import ua.syt0r.kanji.core.file.IosPlatformFileHandler
import ua.syt0r.kanji.core.file.PlatformFileHandler
import ua.syt0r.kanji.core.logger.LoggerConfiguration
import ua.syt0r.kanji.core.sync.IosSyncBackupFileProvider
import ua.syt0r.kanji.core.sync.SyncBackupFileProvider
import ua.syt0r.kanji.core.tts.KanaTtsManager
import ua.syt0r.kanji.core.user_data.database.UserDataDatabaseContract
import ua.syt0r.kanji.core.user_data.preferences.DefaultUserPreferencesMigrationManager
import ua.syt0r.kanji.presentation.IosAccountScreenContent
import ua.syt0r.kanji.presentation.IosGetCreditLibrariesUseCase
import ua.syt0r.kanji.presentation.IosSponsorScreenContent
import ua.syt0r.kanji.presentation.screen.main.screen.account.AccountScreenContract
import ua.syt0r.kanji.presentation.screen.main.screen.credits.GetCreditLibrariesUseCase
import ua.syt0r.kanji.presentation.screen.main.screen.sponsor.SponsorScreenContract

actual val platformComponentsModule: Module = module {

    factory { LoggerConfiguration(true) }

    single<AppDataDatabaseProvider> {
        IosAppDataDatabaseProvider()
    }

    single<SyncBackupFileProvider> {
        IosSyncBackupFileProvider()
    }

    single<DataStore<*>> {

        @OptIn(ExperimentalForeignApi::class)
        val documentDirectory: NSURL = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )!!

        PreferenceDataStoreFactory.createWithPath(
            migrations = DefaultUserPreferencesMigrationManager.DefaultMigrations,
            produceFile = { (documentDirectory.path + "/preferences.preferences_pb").toPath() }
        )

    }

    single<UserDataDatabaseContract.PlatformHandler> {
        IosUserDataDatabasePlatformHandler(
            migrationProvider = get()
        )
    }

    factory<PlatformFileHandler> {
        IosPlatformFileHandler()
    }

    factory<BackupArchiveHandler> { IosBackupArchiveHandler() }

    single<SponsorScreenContract.Content> { IosSponsorScreenContent }
    single<AccountScreenContract.Content> { IosAccountScreenContent }

    factory<GetCreditLibrariesUseCase> { IosGetCreditLibrariesUseCase }

}