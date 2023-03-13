package ua.syt0r.kanji.core.user_data

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.syt0r.kanji.core.user_data.db.UserDataDao
import ua.syt0r.kanji.core.user_data.db.LegacyUserDataDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserDataModule {

    companion object {

        @Provides
        @Singleton
        fun provideUserDataDatabase(app: Application): LegacyUserDataDatabase {
            return LegacyUserDataDatabase.create(app)
        }

        @Provides
        @Singleton
        fun provideDao(database: LegacyUserDataDatabase): UserDataDao {
            return database.dao
        }

    }

    @Binds
    @Singleton
    abstract fun bindWritingRepository(a: AndroidPracticeRepository): UserDataContract.PracticeRepository

}