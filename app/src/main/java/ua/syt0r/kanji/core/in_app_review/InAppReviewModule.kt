package ua.syt0r.kanji.core.in_app_review

import android.app.Application
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InAppReviewModule {

    @Provides
    @Singleton
    fun provideReviewManager(application: Application): InAppReviewManager {
        return InAppReviewManager(
            reviewManager = ReviewManagerFactory.create(application)
        )
    }


}