package ua.syt0r.kanji.core.review

import android.app.Application
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReviewModule {

    @Provides
    @Singleton
    fun provide(application: Application): ReviewManager {
        return PlayServicesReviewManager(
            reviewManager = ReviewManagerFactory.create(application)
        )
    }

}