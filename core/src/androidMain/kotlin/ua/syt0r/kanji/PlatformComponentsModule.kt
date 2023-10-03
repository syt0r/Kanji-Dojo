package ua.syt0r.kanji

import android.app.ActivityManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.work.WorkManager
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import ua.syt0r.kanji.core.AndroidThemeManager
import ua.syt0r.kanji.core.kanji_data.KanjiDatabaseProvider
import ua.syt0r.kanji.core.kanji_data.KanjiDatabaseProviderAndroid
import ua.syt0r.kanji.core.notification.ReminderNotificationContract
import ua.syt0r.kanji.core.notification.ReminderNotificationHandleScheduledActionUseCase
import ua.syt0r.kanji.core.notification.ReminderNotificationManager
import ua.syt0r.kanji.core.notification.ReminderNotificationScheduler
import ua.syt0r.kanji.core.theme_manager.ThemeManager
import ua.syt0r.kanji.core.user_data.AndroidUserPreferencesRepository
import ua.syt0r.kanji.core.user_data.UserDataDatabaseProvider
import ua.syt0r.kanji.core.user_data.UserDataDatabaseProviderAndroid
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository

actual val platformComponentsModule: Module = module {

    single<KanjiDatabaseProvider> {
        KanjiDatabaseProviderAndroid(
            app = androidApplication()
        )
    }

    single<UserDataDatabaseProvider> {
        UserDataDatabaseProviderAndroid(
            app = androidApplication()
        )
    }

    single<UserPreferencesRepository> {
        AndroidUserPreferencesRepository(
            context = androidContext(),
            defaultAnalyticsEnabled = false,
            defaultAnalyticsSuggestionEnabled = false
        )
    }

    single<ThemeManager> {
        val repository: UserPreferencesRepository = get()
        AndroidThemeManager(
            getTheme = { runBlocking { repository.getTheme() } },
            setTheme = { runBlocking { repository.setTheme(it) } }
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