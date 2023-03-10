package ua.syt0r.kanji.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ua.syt0r.kanji.appModules
import ua.syt0r.kanji.core.user_data.LegacyMultiplatformUserPreferencesRepository
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.presentation.androidViewModelModule

@HiltAndroidApp
class KanjiApplication : Application() {

    val tmpModule = module {
        single<UserPreferencesRepository> {
            LegacyMultiplatformUserPreferencesRepository(
                androidContext(),
                defaultAnalyticsEnabled = false,
                defaultAnalyticsSuggestionEnabled = false
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KanjiApplication)
            loadKoinModules(appModules + androidViewModelModule + tmpModule)
        }
    }

}