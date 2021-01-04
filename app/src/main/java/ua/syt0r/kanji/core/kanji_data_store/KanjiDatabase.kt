package ua.syt0r.kanji.core.kanji_data_store

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.syt0r.kanji.core.kanji_data_store.dao.KanjiDatabaseDao
import ua.syt0r.kanji.core.kanji_data_store.entity.CharConverter
import ua.syt0r.kanji.core.kanji_data_store.entity.KanjiDataDbEntity

@Database(entities = [KanjiDataDbEntity::class], version = 1)
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