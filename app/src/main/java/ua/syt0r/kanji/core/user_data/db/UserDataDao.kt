package ua.syt0r.kanji.core.user_data.db

import androidx.room.*
import ua.syt0r.kanji.core.user_data.db.entity.*

@Dao
interface UserDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: PracticeEntity): Long

    @Update
    fun update(entity: PracticeEntity)

    @Query("DELETE FROM practice WHERE id=:id")
    fun deletePractice(id: Long)

    @Query(
        """
        select practice.*, max(timestamp) as latest_review_timestamp
        from practice
        left join writing_review
        on practice.id = writing_review.practice_id  
        group by id
    """
    )
    fun getPracticeSets(): List<ReviewedPracticeEntity>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: PracticeEntryEntity): Long

    @Delete
    fun delete(entity: PracticeEntryEntity)

    @Query("SELECT * FROM practice WHERE id = :practiceId")
    fun getPracticeInfo(practiceId: Long): PracticeEntity

    @Query("SELECT * FROM practice_entry WHERE practice_id = :practiceId")
    fun getPracticeEntries(practiceId: Long): List<PracticeEntryEntity>

    @Query("select character, min(timestamp) as timestamp from writing_review where practice_id = :practiceId and mistakes <= :maxMistakes group by character")
    fun getFirstWritingReviewEntries(
        practiceId: Long,
        maxMistakes: Int
    ): List<LatestWritingReviewCharacterEntity>

    @Query("select character, max(timestamp) as timestamp from writing_review where practice_id = :practiceId and mistakes <= :maxMistakes group by character")
    fun getLastWritingReviewEntries(
        practiceId: Long,
        maxMistakes: Int
    ): List<LatestWritingReviewCharacterEntity>

    @Insert
    fun insert(kanjiWritingReview: WritingReviewEntity)

    @Query("select * from practice inner join writing_review on writing_review.practice_id = practice.id order by timestamp desc limit 1")
    fun getLatestReviewedPractice(): WritingReviewWithPracticeEntity?

    @Query("select count(*) from writing_review")
    fun getReviewedCharactersCount(): Long

}