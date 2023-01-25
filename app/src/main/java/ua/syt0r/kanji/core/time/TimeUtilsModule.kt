package ua.syt0r.kanji.core.time

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TimeUtilsModule {

    @Provides
    fun provideTimeUtils(): TimeUtils = DefaultTimeUtils

}