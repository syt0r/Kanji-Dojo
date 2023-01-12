package ua.syt0r.kanji.core.analytics

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import ua.syt0r.kanji.core.user_data.UserDataContract

@Module
@InstallIn(SingletonComponent::class)
object FdroidAnalyticsModule {

    @Provides
    fun provide(
        application: Application,
        userPreferencesRepository: UserDataContract.PreferencesRepository
    ): AnalyticsManager {
        return MatomoAnalyticsManager(
            isByDefaultEnabled = runBlocking { userPreferencesRepository.getAnalyticsEnabled() },
            context = application
        )
    }

}
