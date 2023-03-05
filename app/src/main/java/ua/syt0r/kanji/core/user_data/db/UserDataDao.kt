package ua.syt0r.kanji.core.user_data.db

import androidx.room.*
import ua.syt0r.kanji.core.user_data.db.entity.*
import java.time.LocalDateTime

@Dao
interface UserDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: PracticeEntity): Long

    @Update
    fun update(entity: PracticeEntity)

    @Query("DELETE FROM practice WHERE id=:id")
    fun deletePractice(id: Long)

    @Query("select * from practice")
    fun getPracticeSets(): List<PracticeEntity>

    @Query("select max(timestamp) from writing_review where practice_id=:practiceId")
    fun getLastWritingReviewTime(practiceId: Long): LocalDateTime?

    @Query("select max(timestamp) from reading_review where practice_id=:practiceId")
    fun getLastReadingReviewTime(practiceId: Long): LocalDateTime?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: PracticeEntryEntity): Long

    @Delete
    fun delete(entity: PracticeEntryEntity)

    @Query("SELECT * FROM practice WHERE id = :practiceId")
    fun getPracticeInfo(practiceId: Long): PracticeEntity

    @Query("SELECT * FROM practice_entry WHERE practice_id = :practiceId")
    fun getPracticeEntries(practiceId: Long): List<PracticeEntryEntity>

    @Insert
    fun insert(kanjiWritingReview: WritingReviewEntity)

    @Insert
    fun insert(entity: ReadingReviewEntity)

    @Query("select count(*) from writing_review")
    fun getReviewedCharactersCount(): Long

    @Query("select * from writing_review where character=:character")
    fun getWritingReviews(character: String): List<WritingReviewEntity>

    @Query("select * from reading_review where character=:character")
    fun getReadingReviews(character: String): List<ReadingReviewEntity>

}