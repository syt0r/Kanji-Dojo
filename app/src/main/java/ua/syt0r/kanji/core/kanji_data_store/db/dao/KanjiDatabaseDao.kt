package ua.syt0r.kanji.core.kanji_data_store.db.dao

import androidx.room.Dao
import androidx.room.Query
import ua.syt0r.kanji_db_model.db.MeaningsTableConstants
import ua.syt0r.kanji_db_model.db.ReadingsTableConstants
import ua.syt0r.kanji_db_model.db.StrokesTableConstants.KANJI_COLUMN
import ua.syt0r.kanji_db_model.db.StrokesTableConstants.STROKE_NUMBER_COLUMN
import ua.syt0r.kanji_db_model.db.StrokesTableConstants.STROKE_PATH_COLUMN
import ua.syt0r.kanji_db_model.db.StrokesTableConstants.TABLE_NAME

@Dao
interface KanjiDatabaseDao {

    @Query("select $KANJI_COLUMN from $TABLE_NAME")
    fun getKanjiList(): List<String>

    @Query("select $STROKE_PATH_COLUMN from $TABLE_NAME where $KANJI_COLUMN = :kanji order by $STROKE_NUMBER_COLUMN")
    fun getStrokes(kanji: String): List<String>

    @Query("select ${ReadingsTableConstants.READING_COLUMN} from ${ReadingsTableConstants.TABLE_NAME} where ${ReadingsTableConstants.KANJI_COLUMN} = :kanji and ${ReadingsTableConstants.READING_TYPE_COLUMN} = :readingType")
    fun getReadings(
        kanji: String,
        readingType: String
    ): List<String>

    @Query("select ${MeaningsTableConstants.MEANING_TYPE_COLUMN} from ${MeaningsTableConstants.TABLE_NAME} where ${MeaningsTableConstants.KANJI_COLUMN} = :kanji")
    fun getMeanings(kanji: String): List<String>

}