package ua.syt0r.kanji.core.kanji_data

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class KanjiDataModule {

    companion object {

        @Provides
        @Singleton
        fun provideRepository(app: Application): KanjiDataRepository {
            val database = KanjiDatabaseProviderAndroid(app).provide()
            return SqlDelightKanjiDataRepository(database.kanjiDataQueries)
        }

    }

}