package ua.syt0r.kanji.core.review

import android.app.Application
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReviewModule {

    companion object {

        @Provides
        @Singleton
        fun provide(application: Application) = ReviewManagerFactory.create(application)

    }

    @Binds
    @Singleton
    abstract fun provideReviewManager(
        manager: PlayServicesReviewManager
    ): ReviewManager

}