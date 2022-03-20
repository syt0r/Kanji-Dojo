package ua.syt0r.kanji.core.kanji_data.db.dao

import androidx.room.Dao
import androidx.room.Query
import ua.syt0r.kanji.core.kanji_data.db.entity.KanjiMeaningEntity
import ua.syt0r.kanji.core.kanji_data.db.entity.KanjiReadingEntity

@Dao
interface KanjiDataDao {

    @Query("select distinct class from classification")
    fun getClassifications(): List<String>


    @Query("select stroke_path from strokes where kanji = :kanji order by stroke_number")
    fun getStrokes(kanji: String): List<String>

    @Query("select * from meanings where kanji = :kanji")
    fun getMeanings(kanji: String): List<KanjiMeaningEntity>

    @Query("select * from readings where kanji = :kanji")
    fun getReading(kanji: String): List<KanjiReadingEntity>

}