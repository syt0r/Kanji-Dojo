package ua.syt0r.kanji.core.user_data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ua.syt0r.kanji.core.user_data.db.entity.PracticeSetEntity
import ua.syt0r.kanji.core.user_data.db.entity.PracticeSetEntryEntity
import ua.syt0r.kanji.core.user_data.db.entity.WritingReviewEntity
import ua.syt0r.kanji.core.user_data.db.entity.WritingReviewWithPracticeEntity

@Dao
interface UserDataDao {

    @Insert
    fun createPracticeSet(entity: PracticeSetEntity): Long

    @Query("DELETE FROM practice_set WHERE id=:id")
    fun deletePracticeSet(id: Long)

    @Query("SELECT * FROM practice_set")
    fun getPracticeSets(): List<PracticeSetEntity>


    @Insert
    fun createPracticeSetEntry(entity: PracticeSetEntryEntity): Long

    @Query("SELECT * FROM practice_set_entry WHERE practice_set_id = :practiceSetId LIMIT 1")
    fun getFirstPracticeSetEntry(practiceSetId: Long): PracticeSetEntryEntity?

    @Query("SELECT * FROM practice_set WHERE id = :practiceId")
    fun getPracticeInfo(practiceId: Long): PracticeSetEntity

    @Query("SELECT * FROM practice_set_entry WHERE practice_set_id = :practiceId")
    fun getPracticeEntries(practiceId: Long): List<PracticeSetEntryEntity>

    @Insert
    fun insert(kanjiWritingReview: WritingReviewEntity)

    @Query("select * from practice_set inner join writing_review on writing_review.practice_set_id = practice_set.id order by timestamp desc limit 1")
    fun getLatestReviewedPractice(): WritingReviewWithPracticeEntity?

}