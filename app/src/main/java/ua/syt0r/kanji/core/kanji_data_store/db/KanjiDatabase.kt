package ua.syt0r.kanji.core.kanji_data_store.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.syt0r.kanji.core.kanji_data_store.db.converters.CharConverter
import ua.syt0r.kanji.core.kanji_data_store.db.dao.KanjiDatabaseDao
import ua.syt0r.kanji.core.kanji_data_store.db.entity.KanjiReadingEntity
import ua.syt0r.kanji.core.kanji_data_store.db.entity.KanjiStrokeEntity

@Database(entities = [KanjiStrokeEntity::class, KanjiReadingEntity::class], version = 1)
@TypeConverters(CharConverter::class)
abstract class KanjiDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "kanji-database"
        private const val DB_ASSET_NAME = "kanji-db.sqlite"

        fun create(context: Context): KanjiDatabase {
            return Room.databaseBuilder(context, KanjiDatabase::class.java, DB_NAME)
                .createFromAsset(DB_ASSET_NAME)
                .allowMainThreadQueries()
                .build()
        }

    }

    abstract fun kanjiDao(): KanjiDatabaseDao

}