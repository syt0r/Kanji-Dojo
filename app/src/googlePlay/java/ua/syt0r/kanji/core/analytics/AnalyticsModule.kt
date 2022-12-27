package ua.syt0r.kanji.core.analytics

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provide(): AnalyticsManager {
        return FirebaseAnalyticsManager(firebaseAnalytics = Firebase.analytics)
    }

}
