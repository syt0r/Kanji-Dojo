package ua.syt0r.kanji.core.kanji_data.db.dao

import androidx.room.Dao
import androidx.room.Query
import ua.syt0r.kanji.core.kanji_data.db.entity.*
import ua.syt0r.kanji_dojo.shared.CharactersClassification

@Dao
interface KanjiDataDao {

    @Query("select stroke_path from strokes where kanji = :kanji order by stroke_number")
    fun getStrokes(kanji: String): List<String>

    @Query("select * from meanings where kanji = :kanji order by priority")
    fun getMeanings(kanji: String): List<KanjiMeaningEntity>

    @Query("select * from readings where kanji = :kanji")
    fun getReading(kanji: String): List<KanjiReadingEntity>

    @Query("select * from data where kanji = :kanji")
    fun getData(kanji: String): KanjiDataEntity?

    @Query("select kanji from data where jlpt = :jlpt")
    fun getKanjiByJLPT(jlpt: CharactersClassification.JLPT): List<String>

    @Query("select kanji from data where grade = :grade")
    fun getKanjiByGrade(grade: Int): List<String>

    @Query("select * from word where expression like '%' || :character || '%'")
    fun getWordsWithCharacter(character: String): List<WordEntity>

    @Query("select * from word_meanings where expression_id = :expressionId order by priority")
    fun getWordMeanings(expressionId: Long): List<WordMeaningEntity>

}