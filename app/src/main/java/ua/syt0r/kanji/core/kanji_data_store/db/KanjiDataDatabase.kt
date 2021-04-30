package ua.syt0r.kanji.core.kanji_data_store.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.syt0r.kanji.core.kanji_data_store.db.converters.CharConverter
import ua.syt0r.kanji.core.kanji_data_store.db.dao.KanjiDataDao
import ua.syt0r.kanji.core.kanji_data_store.db.entity.KanjiClassificationEntity
import ua.syt0r.kanji.core.kanji_data_store.db.entity.KanjiMeaningEntity
import ua.syt0r.kanji.core.kanji_data_store.db.entity.KanjiReadingEntity
import ua.syt0r.kanji.core.kanji_data_store.db.entity.KanjiStrokeEntity

@Database(
    entities = [
        KanjiClassificationEntity::class,
        KanjiMeaningEntity::class,
        KanjiReadingEntity::class,
        KanjiStrokeEntity::class
    ],
    version = 1
)
@TypeConverters(CharConverter::class)
abstract class KanjiDataDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "kanji_data"
        private const val DB_ASSET_NAME = "kanji_data.sqlite"

        fun create(context: Context): KanjiDataDatabase {
            return Room.databaseBuilder(context, KanjiDataDatabase::class.java, DB_NAME)
                .createFromAsset(DB_ASSET_NAME)
                .allowMainThreadQueries() // TODO remove
                .fallbackToDestructiveMigration()
                .build()
        }

    }

    abstract val dao: KanjiDataDao

}