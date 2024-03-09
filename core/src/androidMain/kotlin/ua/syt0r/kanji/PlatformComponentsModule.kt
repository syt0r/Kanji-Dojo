package ua.syt0r.kanji

import android.app.ActivityManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.media3.exoplayer.ExoPlayer
import androidx.work.WorkManager
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.syt0r.kanji.core.AndroidThemeManager
import ua.syt0r.kanji.core.app_data.AppDataDatabaseProvider
import ua.syt0r.kanji.core.app_data.AppDataDatabaseProviderAndroid
import ua.syt0r.kanji.core.backup.AndroidPlatformFileHandler
import ua.syt0r.kanji.core.backup.PlatformFileHandler
import ua.syt0r.kanji.core.notification.ReminderNotificationContract
import ua.syt0r.kanji.core.notification.ReminderNotificationHandleScheduledActionUseCase
import ua.syt0r.kanji.core.notification.ReminderNotificationManager
import ua.syt0r.kanji.core.notification.ReminderNotificationScheduler
import ua.syt0r.kanji.core.suspended_property.DataStoreSuspendedPropertyProvider
import ua.syt0r.kanji.core.suspended_property.SuspendedPropertyProvider
import ua.syt0r.kanji.core.theme_manager.ThemeManager
import ua.syt0r.kanji.core.tts.AndroidKanaTtsManager
import ua.syt0r.kanji.core.tts.KanaTtsManager
import ua.syt0r.kanji.core.tts.Neural2BKanaVoiceData
import ua.syt0r.kanji.core.user_data.UserDataDatabaseManager
import ua.syt0r.kanji.core.user_data.UserDataDatabaseManagerAndroid
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository

val userPreferencesDataStoreQualifier = named("user_preferences_data_store")

actual val platformComponentsModule: Module = module {

    factory { ExoPlayer.Builder(androidContext()).build() }

    factory<KanaTtsManager> {
        AndroidKanaTtsManager(
            player = get(),
            voiceData = Neural2BKanaVoiceData
        )
    }

    single<AppDataDatabaseProvider> {
        AppDataDatabaseProviderAndroid(
            app = androidApplication()
        )
    }

    single<UserDataDatabaseManager> {
        UserDataDatabaseManagerAndroid(
            app = androidApplication()
        )
    }

    factory<PlatformFileHandler> {
        AndroidPlatformFileHandler(
            contentResolver = androidContext().contentResolver
        )
    }

    single<DataStore<Preferences>>(qualifier = userPreferencesDataStoreQualifier) {
        PreferenceDataStoreFactory.create {
            androidContext().preferencesDataStoreFile("preferences")
        }
    }

    factory<SuspendedPropertyProvider> {
        DataStoreSuspendedPropertyProvider(
            dataStore = get(qualifier = userPreferencesDataStoreQualifier)
        )
    }

    single<ThemeManager> {
        val repository: UserPreferencesRepository = get()
        AndroidThemeManager(
            getTheme = { runBlocking { repository.theme.get() } },
            setTheme = { runBlocking { repository.theme.set(it) } }
        )
    }

    factory<WorkManager> { WorkManager.getInstance(androidContext()) }
    factory<NotificationManagerCompat> { NotificationManagerCompat.from(androidContext()) }
    factory<ActivityManager> { androidContext().getSystemService<ActivityManager>()!! }

    factory<ReminderNotificationContract.Scheduler> {
        ReminderNotificationScheduler(
            workManger = get(),
            timeUtils = get()
        )
    }

    factory<ReminderNotificationContract.Manager> {
        ReminderNotificationManager(
            context = androidContext(),
            notificationManager = get()
        )
    }

    factory<ReminderNotificationContract.HandleScheduledActionUseCase> {
        ReminderNotificationHandleScheduledActionUseCase(
            activityManager = get(),
            appStateManager = get(),
            notificationManager = get(),
            repository = get(),
            scheduler = get(),
            analyticsManager = get()
        )
    }

}