package ua.syt0r.kanji.core.kanji_data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.syt0r.kanji.core.kanji_data.db.converters.FuriganaConverter
import ua.syt0r.kanji.core.kanji_data.db.converters.ReadingTypeConverter
import ua.syt0r.kanji.core.kanji_data.db.dao.KanjiDataDao
import ua.syt0r.kanji.core.kanji_data.db.entity.*

@Database(
    entities = [
        KanjiDataEntity::class,
        KanjiMeaningEntity::class,
        KanjiReadingEntity::class,
        KanjiStrokeEntity::class,
        WordEntity::class,
        WordReadingEntity::class,
        WordMeaningEntity::class,
        CharacterRadicalEntity::class
    ],
    version = 2
)
@TypeConverters(
    FuriganaConverter::class,
    ReadingTypeConverter::class
)
abstract class KanjiDataDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "kanji_data"
        private const val DB_ASSET_NAME = "kanji_data.sqlite"

        fun create(context: Context): KanjiDataDatabase {
            return Room.databaseBuilder(context, KanjiDataDatabase::class.java, DB_NAME)
                .createFromAsset(DB_ASSET_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }

    }

    abstract val dao: KanjiDataDao

}