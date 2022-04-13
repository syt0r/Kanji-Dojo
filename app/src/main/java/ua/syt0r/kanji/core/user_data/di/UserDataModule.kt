package ua.syt0r.kanji.core.user_data.di

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.syt0r.kanji.core.user_data.UserDataContract
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.core.user_data.PracticeRepository
import ua.syt0r.kanji.core.user_data.db.UserDataDao
import ua.syt0r.kanji.core.user_data.db.UserDataDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserDataModule {

    companion object {

        @Provides
        fun provideUserDataDatabase(app: Application): UserDataDatabase {
            return UserDataDatabase.create(app)
        }

        @Provides
        fun provideDao(database: UserDataDatabase): UserDataDao {
            return database.dao
        }

    }

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        a: UserPreferencesRepository
    ): UserDataContract.PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindWritingRepository(a: PracticeRepository): UserDataContract.PracticeRepository

}