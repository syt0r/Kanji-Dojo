package ua.syt0r.kanji.core.kanji_data.db.dao

import androidx.room.Dao
import androidx.room.Query
import ua.syt0r.kanji.common.CharactersClassification
import ua.syt0r.kanji.core.kanji_data.db.entity.*

@Dao
interface KanjiDataDao {

    @Query("select stroke_path from strokes where kanji = :kanji order by stroke_number")
    fun getStrokes(kanji: String): List<String>

    @Query("select character_radicals.* from character_radicals where character = :character order by start_stroke")
    fun getCharacterRadicals(character: String): List<CharacterRadicalEntity>

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

    @Query("select * from dic_meaning where dic_entry_id = :dictionaryEntryId order by priority")
    fun getWordMeanings(dictionaryEntryId: Long): List<WordMeaningEntity>

    @Query(
        """
        select *
        from dic_reading
        where kana_expression like :characterQuery 
        and length(kana_expression) > 1 
        and expression is not null
        order by rank
        """
    )
    fun getKanaWordReadings(characterQuery: String): List<WordReadingEntity>

    @Query("select * from dic_reading where expression like '%' || :character || '%' order by rank")
    fun getWordReadings(character: String): List<WordReadingEntity>

}