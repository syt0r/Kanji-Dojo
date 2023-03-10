package ua.syt0r.kanji.core.user_data

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FdroidUserPreferencesModule {

    @Provides
    @Singleton
    fun provideUserPreferences(app: Application): UserDataContract.PreferencesRepository {
        return LegacyUserPreferencesRepository(
            context = app,
            defaultAnalyticsEnabled = false,
            defaultAnalyticsSuggestionEnabled = true
        )
    }

}