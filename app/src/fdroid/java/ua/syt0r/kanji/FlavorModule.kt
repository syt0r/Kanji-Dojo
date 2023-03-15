package ua.syt0r.kanji

import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.analytics.MatomoAnalyticsManager
import ua.syt0r.kanji.core.user_data.AndroidUserPreferencesRepository
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository

val flavorModule = module {

    single<AnalyticsManager> {
        MatomoAnalyticsManager(
            isByDefaultEnabled = false,// runBlocking { userPreferencesRepository.getAnalyticsEnabled() },
            context = androidContext()
        )
    }

    single<UserPreferencesRepository> {
        AndroidUserPreferencesRepository(
            context = androidApplication(),
            defaultAnalyticsEnabled = false,
            defaultAnalyticsSuggestionEnabled = true
        )
    }

}