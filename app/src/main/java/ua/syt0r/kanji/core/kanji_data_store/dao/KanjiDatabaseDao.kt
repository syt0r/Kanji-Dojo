package ua.syt0r.kanji.core.kanji_data_store.dao

import androidx.room.Dao
import androidx.room.Query
import ua.syt0r.kanji.core.kanji_data_store.KanjiDataStoreContract
import ua.syt0r.kanji_model.db.StrokesTableConstants.KANJI_COLUMN
import ua.syt0r.kanji_model.db.StrokesTableConstants.STROKE_NUMBER_COLUMN
import ua.syt0r.kanji_model.db.StrokesTableConstants.STROKE_PATH_COLUMN
import ua.syt0r.kanji_model.db.StrokesTableConstants.TABLE_NAME

@Dao
interface KanjiDatabaseDao : KanjiDataStoreContract.DataStore {

    @Query("select $KANJI_COLUMN from $TABLE_NAME")
    override fun getKanjiList(): List<String>

    @Query("select $STROKE_PATH_COLUMN from $TABLE_NAME where $KANJI_COLUMN = :kanji order by $STROKE_NUMBER_COLUMN")
    override fun getStrokes(kanji: String): List<String>

}