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
import ua.syt0r.kanji.core.user_data.db.entity.ReadingReviewEntity
import ua.syt0r.kanji.core.user_data.db.entity.WritingReviewEntity

@Database(
    entities = [
        PracticeEntity::class,
        PracticeEntryEntity::class,
        WritingReviewEntity::class,
        ReadingReviewEntity::class
    ],
    version = 3
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class LegacyUserDataDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "user_data"

        fun create(context: Context): LegacyUserDataDatabase {
            return Room.databaseBuilder(context, LegacyUserDataDatabase::class.java, DB_NAME)
                .addMigrations(UserDataMigration1To2, UserDataMigration2To3)
                .build()
        }

    }

    abstract val dao: UserDataDao


    private object UserDataMigration1To2 : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE writing_review ADD is_study INTEGER NOT NULL DEFAULT 1")
        }
    }

    private object UserDataMigration2To3 : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `reading_review` (`character` TEXT NOT NULL, `practice_id` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `mistakes` INTEGER NOT NULL, PRIMARY KEY(`character`, `practice_id`, `timestamp`, `mistakes`), FOREIGN KEY(`practice_id`) REFERENCES `practice`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        }
    }

}