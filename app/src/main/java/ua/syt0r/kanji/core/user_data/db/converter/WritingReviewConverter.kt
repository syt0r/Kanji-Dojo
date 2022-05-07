package ua.syt0r.kanji.core.user_data.db.converter

import androidx.room.TypeConverter
import ua.syt0r.kanji.core.user_data.db.entity.WritingReviewEntity
import ua.syt0r.kanji.core.user_data.model.CharacterReviewResult

object WritingReviewConverter {

    @TypeConverter
    fun convert(reviewResult: CharacterReviewResult) = reviewResult.run {
        WritingReviewEntity(character, practiceSetId, reviewTime, mistakes)
    }

    @TypeConverter
    fun convert(review: WritingReviewEntity) = review.run {
        CharacterReviewResult(character, practiceSetId, reviewTime, mistakes)
    }

}