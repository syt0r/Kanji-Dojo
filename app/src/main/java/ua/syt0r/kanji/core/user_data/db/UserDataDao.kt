package ua.syt0r.kanji.core.user_data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ua.syt0r.kanji.core.user_data.db.entity.PracticeSetEntity
import ua.syt0r.kanji.core.user_data.db.entity.PracticeSetEntryEntity

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
    fun getPracticeSetEntryBy(practiceSetId: Long): PracticeSetEntryEntity?

}