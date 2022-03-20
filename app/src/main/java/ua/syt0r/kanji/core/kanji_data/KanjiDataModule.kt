package ua.syt0r.kanji.core.kanji_data

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.syt0r.kanji.core.kanji_data.KanjiDataContract
import ua.syt0r.kanji.core.kanji_data.KanjiDataRepository
import ua.syt0r.kanji.core.kanji_data.db.KanjiDataDatabase
import ua.syt0r.kanji.core.kanji_data.db.dao.KanjiDataDao

@Module
@InstallIn(SingletonComponent::class)
abstract class KanjiDataModule {

    companion object {

        @Provides
        fun provideDatabase(app: Application): KanjiDataDatabase {
            return KanjiDataDatabase.create(app)
        }

        @Provides
        fun provideDao(database: KanjiDataDatabase): KanjiDataDao {
            return database.dao
        }

    }

    @Binds
    abstract fun provideRepository(database: KanjiDataRepository): KanjiDataContract.Repository

}