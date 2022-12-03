package ua.syt0r.kanji.core.user_data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ua.syt0r.kanji.core.user_data.db.converter.LocalDateTimeConverter
import ua.syt0r.kanji.core.user_data.db.entity.PracticeEntity
import ua.syt0r.kanji.core.user_data.db.entity.PracticeEntryEntity
import ua.syt0r.kanji.core.user_data.db.entity.WritingReviewEntity

@Database(
    entities = [
        PracticeEntity::class,
        PracticeEntryEntity::class,
        WritingReviewEntity::class
    ],
    version = 2
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class UserDataDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "user_data"

        fun create(context: Context): UserDataDatabase {
            return Room.databaseBuilder(context, UserDataDatabase::class.java, DB_NAME)
                .addMigrations(UserDataMigration1To2)
                .build()
        }

    }

    abstract val dao: UserDataDao


    object UserDataMigration1To2 : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE writing_review ADD is_study INTEGER NOT NULL DEFAULT 1")
        }
    }

}